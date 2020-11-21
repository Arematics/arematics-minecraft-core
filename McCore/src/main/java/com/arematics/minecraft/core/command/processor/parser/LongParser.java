package com.arematics.minecraft.core.command.processor.parser;

import org.springframework.stereotype.Component;

@Component
public class LongParser extends CommandParameterParser<Long> {

    @Override
    public Long parse(String value) throws CommandProcessException {
        long parsed;
        try{
            parsed = Long.parseLong(value);
        }catch (NumberFormatException nfe){
            throw new CommandProcessException(value + " must be a number");
        }
        return parsed;
    }
}
