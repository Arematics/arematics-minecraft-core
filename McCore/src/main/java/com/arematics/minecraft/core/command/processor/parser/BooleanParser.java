package com.arematics.minecraft.core.command.processor.parser;

import org.springframework.stereotype.Component;

@Component
public class BooleanParser extends CommandParameterParser<Boolean> {

    @Override
    public Boolean parse(String value) throws ParserException {
        return Boolean.parseBoolean(value);
    }
}
