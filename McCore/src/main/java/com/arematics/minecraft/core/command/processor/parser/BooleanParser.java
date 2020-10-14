package com.arematics.minecraft.core.command.processor.parser;

import com.arematics.minecraft.core.annotations.Parser;

@Parser
public class BooleanParser extends CommandParameterParser<Boolean> {

    @Override
    public Boolean doParse(String value) throws ParserException {
        return Boolean.parseBoolean(value);
    }
}
