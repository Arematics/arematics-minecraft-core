package com.arematics.minecraft.crystals.logic;

import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.CrystalKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class CrystalMetaParser {

    private final Map<String, CrystalType> types = new HashMap<>();

    @Autowired
    public CrystalMetaParser(List<CrystalType> types){
        types.forEach(type -> this.types.put(type.propertyValue(), type));
    }

    public void parse(CorePlayer player, CoreItem coreItem, CrystalKey crystalKey) throws RuntimeException{
        Optional<String> key = this.types.keySet().stream().filter(val -> coreItem.getMeta().hasKey(val)).findFirst();
        if(!key.isPresent())
            throw new CommandProcessException("No valid Data Type found. Give back crystal key");
        else
            this.types.get(key.get()).execute(player, coreItem, crystalKey);
    }
}
