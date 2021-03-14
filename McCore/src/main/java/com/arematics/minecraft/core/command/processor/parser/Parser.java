package com.arematics.minecraft.core.command.processor.parser;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.annotations.SkipEnum;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.anvil.AnvilGUI;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
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

    public Object[] fillParameters(CorePlayer sender, String[] annotation, Parameter[] subParameters, String[] src)
            throws CommandProcessException, InterruptedException {
        List<Object> parameters = new ArrayList<>();
        parseSender(parameters, sender, subParameters[0]);
        int b = 1;
        for(int i = 0; i < annotation.length; i++){
            String parameter = annotation[i];
            if(parameter.startsWith("{") && parameter.endsWith("}")){
                parseParameter(parameters, sender, parameter, src[i], subParameters[b]);
                b++;
            }
        }

        return parameters.toArray(new Object[]{});
    }

    private void parseParameter(List<Object> parameters, CorePlayer sender, String parameter, String source,
                                Parameter subParameter)
            throws CommandProcessException, InterruptedException {
        if(subParameter.getType().isEnum() && !subParameter.isAnnotationPresent(SkipEnum.class))
            parseEnum(parameters, sender, parameter, source, subParameter);
        else
            parseClass(parameters, sender, parameter, source, subParameter);
    }

    private void parseEnum(List<Object> parameters, CorePlayer sender, String parameter, String source,
                           Parameter subParameter)
            throws CommandProcessException, InterruptedException {
        try{
            parameters.add(Enum.valueOf((Class)subParameter.getType(), source));
        }catch (Exception e){
            if(source.equals(parameter)){
                String result = awaitAnvilResult(parameter
                        .replace("{", "")
                        .replace("}", ""), sender);
                if(result == null) throw new CommandProcessException("Command input was interrupt by player");
                sender.info("Parameter " + parameter + " replaced with " + result).handle();
                parameters.add(Enum.valueOf((Class) subParameter.getType(), result));
            }else throw new CommandProcessException("Not valid parameter value type");
        }
    }

    private void parseClass(List<Object> parameters, CorePlayer sender, String parameter, String source,
                            Parameter subParameter)
            throws CommandProcessException, InterruptedException {
        CommandParameterParser<?> parser = parsers.get(subParameter.getType());
        if(source.equals(parameter)){
            String result = awaitAnvilResult(parameter
                    .replace("{", "")
                    .replace("}", ""), sender);
            if(result == null) throw new CommandProcessException("Command input was interrupt by player");
            sender.info("Parameter " + parameter + " replaced with " + result).handle();
            parameters.add(parser.processParse(result, subParameter, parameters));
        }else {
            parameters.add(parser.processParse(source, subParameter, parameters));
        }
    }

    private void parseSender(List<Object> parameters, CorePlayer sender, Parameter subParameter)
            throws CommandProcessException{
        CommandParameterParser<?> parser = parsers.get(subParameter.getType());
        if(!(parser instanceof CommandSenderParser))
            throw new CommandProcessException("No correct sender parser could be found");
        CommandSenderParser<?> senderParser = (CommandSenderParser<?>) parser;
        parameters.add(senderParser.processParse(sender, subParameter, parameters));
    }

    private String awaitAnvilResult(String key, CorePlayer player) throws InterruptedException {
        return ArematicsExecutor.awaitResult((res, latch) ->
                new AnvilGUI(Boots.getBoot(CoreBoot.class), player.getPlayer(), key + ": ", (p, result) -> {
                    res.set(result);
                    latch.countDown();
                    return null;
                }));
    }
}
