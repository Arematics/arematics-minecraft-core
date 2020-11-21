package com.arematics.minecraft.core.command.processor.parser;

import org.springframework.stereotype.Component;

@Component
public class IntegerParser extends CommandParameterParser<Integer> {

    @Override
    public Integer parse(String value) throws CommandProcessException {
        try{
            return Integer.parseInt(value);
        }catch (NumberFormatException e){
            throw new CommandProcessException(value + " must be a number");
        }
    }
}
