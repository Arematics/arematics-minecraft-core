package com.arematics.minecraft.kits.commands.parser;

import com.arematics.minecraft.core.command.processor.parser.CommandParameterParser;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.data.mode.model.Kit;
import com.arematics.minecraft.data.service.KitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KitParser extends CommandParameterParser<Kit> {

    private final KitService service;

    @Autowired
    public KitParser(KitService kitService){
        this.service = kitService;
    }

    @Override
    public Kit parse(String name) throws CommandProcessException {
        try{
            return service.findKit(name);
        }catch (RuntimeException re){
            throw new CommandProcessException("Kit with name: " + name + " could not be found");
        }
    }
}
