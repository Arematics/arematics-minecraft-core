package com.arematics.minecraft.core.command.processor.parser;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.inventories.anvil.AnvilGUI;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Parser {

    public static Parser getInstance(){
        return Boots.getBoot(CoreBoot.class).getContext().getBean(Parser.class);
    }

    private final Map<Object, CommandParameterParser<?>> parsers = new HashMap<>();

    @Autowired
    public Parser(List<CommandParameterParser<?>> parsers){
        parsers.forEach(this::addParser);
    }

    public void addParser(CommandParameterParser<?> parser){
        if(!parsers.containsKey(parser.getType())) parsers.put(parser.getType(), parser);
    }

    public Object[] fillParameters(CommandSender sender, String[] annotation, Parameter[] subParameters, String[] src)
            throws ParserException, InterruptedException {
        List<Object> parameters = new ArrayList<>();
        if (CommandSender.class.equals(subParameters[0].getType())) {
            parameters.add(sender);
        } else if (CorePlayer.class.equals(subParameters[0].getType())) {
            if (sender instanceof Player) {
                parameters.add(CorePlayer.get((Player)sender));
            } else {
                throw new ParserException("Only Players allowed to perform this command");
            }
        }else if (Player.class.equals(subParameters[0].getType())) {
            if (sender instanceof Player) {
                parameters.add(sender);
            } else {
                throw new ParserException("Only Players allowed to perform this command");
            }
        }
        int b = 1;
        for(int i = 0; i < annotation.length; i++){
            String parameter = annotation[i];
            if(parameter.startsWith("{") && parameter.endsWith("}")){
                try{
                    parameters.add(Enum.valueOf((Class)subParameters[b].getType(), src[i]));
                }catch (Exception exception){
                    if(subParameters[b].getType().isEnum()){
                        if(src[i].equals(parameter) && sender instanceof Player){
                            String result = awaitAnvilResult((Player)sender);
                            Messages.create("Parameter " + parameter + " replaced with " + result).to(sender).handle();
                            parameters.add(Enum.valueOf((Class) subParameters[b].getType(), result));
                        }
                        throw new ParserException("Not valid parameter value type");
                    }
                    CommandParameterParser<?> parser = parsers.get(subParameters[b].getType());
                    if(src[i].equals(parameter) && sender instanceof Player){
                        String result = awaitAnvilResult((Player)sender);
                        Messages.create("Parameter " + parameter + " replaced with " + result).to(sender).handle();
                        parameters.add(parser.processParse(result, subParameters[b], parameters));
                    }else {
                        parameters.add(parser.processParse(src[i], subParameters[b], parameters));
                    }
                }
                b++;
            }
        }

        return parameters.toArray(new Object[]{});
    }

    private String awaitAnvilResult(Player player) throws InterruptedException {
        return ArematicsExecutor.awaitResult((res, latch) -> {
            new AnvilGUI(Boots.getBoot(CoreBoot.class), player, "Replace this", (p, result) -> {
                res.set(result);
                latch.countDown();
                return null;
            });
        });
    }
}
