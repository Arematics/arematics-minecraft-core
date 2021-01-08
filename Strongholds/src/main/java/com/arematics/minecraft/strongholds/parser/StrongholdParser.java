package com.arematics.minecraft.strongholds.parser;

import com.arematics.minecraft.core.command.processor.parser.CommandParameterParser;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.data.mode.model.Stronghold;
import com.arematics.minecraft.data.service.StrongholdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StrongholdParser extends CommandParameterParser<Stronghold> {

    private final StrongholdService service;

    @Autowired
    public StrongholdParser(StrongholdService strongholdService){
        this.service = strongholdService;
    }

    @Override
    public Stronghold parse(String value) throws CommandProcessException {
        try{
            return service.findById(value);
        }catch (RuntimeException re){
            throw new CommandProcessException("Stronghold with id: " + value + " could not be found");
        }
    }
}
