package com.arematics.minecraft.core.command.processor.parser;

import org.springframework.stereotype.Component;

@Component
public class DoubleParser extends CommandParameterParser<Double>{
    @Override
    public Double parse(String value) throws CommandProcessException {
        try{
            return Double.parseDouble(value);
        }catch (NumberFormatException e){
            throw new CommandProcessException(value + " must be a number");
        }
    }
}
