package com.arematics.minecraft.crystals.commands.parser;

import com.arematics.minecraft.core.command.processor.parser.CommandParameterParser;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.data.mode.model.CrystalKey;
import com.arematics.minecraft.data.service.CrystalKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CrystalKeyParser extends CommandParameterParser<CrystalKey> {

    private final CrystalKeyService service;

    @Autowired
    public CrystalKeyParser(CrystalKeyService crystalKeyService){
        this.service = crystalKeyService;
    }

    @Override
    public CrystalKey parse(String value) throws CommandProcessException {
        try{
            return service.findById(value);
        }catch (RuntimeException re){
            throw new CommandProcessException(re.getMessage());
        }
    }
}
