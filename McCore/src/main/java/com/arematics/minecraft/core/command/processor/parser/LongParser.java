package com.arematics.minecraft.core.command.processor.parser;

import org.springframework.stereotype.Component;

@Component
public class LongParser extends CommandParameterParser<Long> {

    @Override
    public Long parse(String value) throws ParserException {
        long parsed;
        try{
            parsed = Long.parseLong(value);
        }catch (NumberFormatException nfe){
            throw new ParserException(value + " must be a number");
        }
        return parsed;
    }
}
