package com.arematics.minecraft.strongholds.commands.parser;

import com.arematics.minecraft.core.command.processor.parser.CommandParameterParser;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.times.TimeUtils;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;

@Component
public class DayParser extends CommandParameterParser<DayOfWeek> {

    @Override
    public DayOfWeek parse(String value) throws CommandProcessException {
        try{
            return TimeUtils.fromDayString(value);
        }catch (Exception e){
            throw new CommandProcessException("Not a valid week day");
        }
    }
}
