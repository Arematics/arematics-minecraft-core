package com.arematics.minecraft.core.command.processor.parser;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class LocalDateParser extends CommandParameterParser<LocalDate>{

    private final DateTimeFormatter[] PATTERNS = {
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("dd.M.yyyy"),
            DateTimeFormatter.ofPattern("d.MM.yyyy"),
            DateTimeFormatter.ofPattern("d.M.yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd-M-yyyy"),
            DateTimeFormatter.ofPattern("d-MM-yyyy"),
            DateTimeFormatter.ofPattern("d-M-yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd/M/yyyy"),
            DateTimeFormatter.ofPattern("d/MM/yyyy"),
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-d"),
            DateTimeFormatter.ofPattern("yyyy-M-d"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy-M-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/d"),
            DateTimeFormatter.ofPattern("yyyy/M/d"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("yyyy/M/dd"),
    };

    @Override
    public LocalDate parse(String value) throws CommandProcessException {
        LocalDate date = null;
        for(DateTimeFormatter formatter: PATTERNS){
            try{
                date = LocalDate.parse(value, formatter);
            }catch (DateTimeParseException | IllegalArgumentException ignore){}
            if(date != null) break;
        }
        if(date == null) throw new CommandProcessException(value + " is not valid LocalDate");
        return date;
    }
}
