package com.arematics.minecraft.clans.commands.parser;

import com.arematics.minecraft.core.command.processor.parser.CommandParameterParser;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.data.mode.model.Clan;
import com.arematics.minecraft.data.service.ClanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClanParser extends CommandParameterParser<Clan> {

    private final ClanService service;

    @Autowired
    public ClanParser(ClanService clanService){
        this.service = clanService;
    }

    @Override
    public Clan parse(String value) throws CommandProcessException {
        try{
            return service.findClanByName(value);
        }catch (RuntimeException re){
            try{
                return service.findClanByTag(value);
            }catch (RuntimeException re2){
                return service.findClanById(Long.parseLong(value));
            }
        }
    }
}
