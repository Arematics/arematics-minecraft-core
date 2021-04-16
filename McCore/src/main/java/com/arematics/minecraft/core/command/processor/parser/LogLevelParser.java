package com.arematics.minecraft.core.command.processor.parser;

import org.springframework.stereotype.Component;

import java.util.logging.Level;

@Component
public class LogLevelParser extends CommandParameterParser<Level> {

    @Override
    public Level parse(String value) throws CommandProcessException {
        try{
            return Level.parse(value);
        }catch (Exception e){
            throw new CommandProcessException("Invalid logging level");
        }
    }
}
