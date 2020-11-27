package com.arematics.minecraft.core.command.processor.parser;

import org.springframework.stereotype.Component;

@Component
public class ByteParser extends CommandParameterParser<Byte> {
    @Override
    public Byte parse(String value) throws CommandProcessException {
        try{
            return Byte.parseByte(value);
        }catch (NumberFormatException nfe){
            throw new CommandProcessException("Number between 0 and 255 allowed");
        }
    }
}
