package com.arematics.minecraft.core.command.processor.parser;

import com.arematics.minecraft.core.Engine;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {

    public static Parser getInstance(){
        return Engine.getInstance().getParser();
    }

    private final Map<Object, CommandParameterParser<?>> parsers = new HashMap<>();

    public Parser(){
        this.addDefaultParser();
    }

    private void addDefaultParser(){
        addParser(new StringParser());
        addParser(new IntegerParser());
        addParser(new DoubleParser());
        addParser(new DateParser());
        addParser(new LocalDateTimeParser());
    }

    public void addParser(CommandParameterParser<?> parser){
        if(!parsers.containsKey(parser.getType())) parsers.put(parser.getType(), parser);
    }

    public Object[] fillParameters(CommandSender sender, String[] annotation, Class[] types, String[] src)
            throws ParserException {
        List<Object> parameters = new ArrayList<>();
        parameters.add(sender);
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
