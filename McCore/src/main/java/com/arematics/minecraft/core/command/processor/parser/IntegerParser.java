package com.arematics.minecraft.core.command.processor.parser;

import org.springframework.stereotype.Component;

@Component
public class IntegerParser extends CommandParameterParser<Integer> {

    @Override
    public Integer parse(String value) throws ParserException {
        try{
            return Integer.parseInt(value);
        }catch (NumberFormatException e){
            throw new ParserException(value + " must be a number");
        }
    }
}
