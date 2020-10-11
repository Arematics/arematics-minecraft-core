package com.arematics.minecraft.core.command.processor.parser;

import com.arematics.minecraft.core.annotations.Parser;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

@Parser
public class PeriodParser extends CommandParameterParser<Period> {

    @Override
    public Period doParse(String timeInput) throws ParserException {
        if(timeInput.startsWith("-")) throw new ParserException("No negative times allowed");
        Period period;
        try{
            period = Period.parse(timeInput);
        }catch (Exception exception){
            try{
                PeriodFormatter formatter = new PeriodFormatterBuilder()
                        .appendDays().appendSuffix("D")
                        .appendHours().appendSuffix("H")
                        .appendMinutes().appendSuffix("M")
                        .appendSeconds().appendSuffix("S")
                        .toFormatter();

                period = formatter.parsePeriod(timeInput);
            }catch (IllegalArgumentException dtpe2){
                throw new ParserException("Not valid time input please use: %D%H%M%S");
            }
        }
        return period;
    }
}
