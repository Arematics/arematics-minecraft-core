package com.arematics.minecraft.core.command.processor.parser;

import com.arematics.minecraft.core.times.TimeUtils;

import java.time.DayOfWeek;
import java.util.List;

public class DayOfWeekListParser extends CommandParameterParser<List<DayOfWeek>> {
    @Override
    public List<DayOfWeek> parse(String value) throws CommandProcessException {
        try{
            return TimeUtils.fromDaysString(value);
        }catch (Exception e){
            throw new CommandProcessException("Day list is not correct formed");
        }
    }
}
