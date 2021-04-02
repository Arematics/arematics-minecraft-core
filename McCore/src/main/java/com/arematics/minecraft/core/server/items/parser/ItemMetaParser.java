package com.arematics.minecraft.core.server.items.parser;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ItemMetaParser {

    private final Map<String, ItemType> types = new HashMap<>();
    private final DefaultItemType defaultItemType;

    @Autowired
    public ItemMetaParser(List<ItemType> types, DefaultItemType defaultItemType){
        types.forEach(type -> this.types.put(type.propertyValue(), type));
        this.defaultItemType = defaultItemType;
    }

    public Map<String, ItemType> getTypes() {
        return types;
    }

    public Part parse(CorePlayer player, CoreItem coreItem) throws RuntimeException  {
        Optional<String> key = this.types.keySet().stream().filter(val -> coreItem.getMeta().hasKey(val)).findFirst();
        if(!key.isPresent()) return defaultItemType.execute(player, coreItem);
        else return this.types.get(key.get()).execute(player, coreItem);
    }
}
