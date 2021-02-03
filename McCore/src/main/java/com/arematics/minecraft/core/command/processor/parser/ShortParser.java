package com.arematics.minecraft.core.command.processor.parser;

import org.springframework.stereotype.Component;

@Component
public class ShortParser extends CommandParameterParser<Short> {
    @Override
    public Short parse(String value) throws CommandProcessException {
        try{
            return Short.parseShort(value);
        }catch (NumberFormatException nfe){
            throw new CommandProcessException("Number between 0 and 32767 allowed");
        }
    }
}
