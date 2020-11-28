package com.arematics.minecraft.crystals.logic;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.CorePlayer;
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

    public void parse(CorePlayer player, CoreItem coreItem) throws RuntimeException{
        Optional<String> key = this.types.keySet().stream().filter(val -> coreItem.getMeta().hasKey(val)).findFirst();
        if(!key.isPresent())
            player.failure("Type of Crystal Key is not registered yet").handle();
        else
            this.types.get(key.get()).execute(player);
    }
}
