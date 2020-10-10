package com.arematics.minecraft.core.command.processor.parser;

import com.arematics.minecraft.core.annotations.Parser;

@Parser
public class LongParser extends CommandParameterParser<Long> {

    @Override
    public Long doParse(String value) throws ParserException {
        long parsed;
        try{
            parsed = Long.parseLong(value);
        }catch (NumberFormatException nfe){
            throw new ParserException(value + " must be a number");
        }
        return parsed;
    }
}
