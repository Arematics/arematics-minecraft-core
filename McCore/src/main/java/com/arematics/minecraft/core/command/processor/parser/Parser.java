package com.arematics.minecraft.core.command.processor.parser;

import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.Boots;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        System.out.println(parser.getType());
        if(!parsers.containsKey(parser.getType())) parsers.put(parser.getType(), parser);
    }

    public Object[] fillParameters(CommandSender sender, String[] annotation, Class[] types, String[] src)
            throws ParserException {
        List<Object> parameters = new ArrayList<>();
        if (CommandSender.class.equals(types[0])) {
            parameters.add(sender);
        } else if (Player.class.equals(types[0])) {
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
                    parameters.add(Enum.valueOf(types[b], src[i]));
                }catch (Exception exception){
                    if(types[b].isEnum()){
                        throw new ParserException("Not valid parameter value type");
                    }
                    CommandParameterParser parser = parsers.get(types[b]);
                    parameters.add(parser.doParse(src[i]));
                }
                b++;
            }
        }

        return parameters.toArray(new Object[]{});
    }
}
