package com.arematics.minecraft.core.command.processor.parser;

import org.springframework.stereotype.Component;

import java.sql.Time;

@Component
public class TimeParser extends CommandParameterParser<Time> {

    @Override
    public Time parse(String value) throws CommandProcessException {
        try{
            return Time.valueOf(value);
        }catch (NumberFormatException nfe){
            throw new CommandProcessException("Not valid time format");
        }
    }
}
