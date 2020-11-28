package com.arematics.minecraft.crystals.commands.parser;

import com.arematics.minecraft.core.command.processor.parser.CommandParameterParser;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.data.mode.model.CrystalKey;
import com.arematics.minecraft.data.service.CrystalKeyService;
import org.bukkit.entity.ArmorStand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
            throw new CommandProcessException("No crystal key with name: " + value + " could be found");
        }
    }

    public Optional<CrystalKey> readFromArmorStand(ArmorStand armorStand){
        return service.findAllNames().stream()
                .filter(crystal -> armorStand.getCustomName().contains(crystal))
                .findFirst()
                .map(this::parse)
                .filter(key -> armorStand.getCustomName().equals(key.getTotalName()));
    }

    public Optional<CrystalKey> readFromItem(CoreItem coreItem){
        return Optional.of(parse(coreItem.getMeta().getString("crystal")));
    }
}
