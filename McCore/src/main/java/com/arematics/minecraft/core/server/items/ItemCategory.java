package com.arematics.minecraft.core.server.items;

import com.arematics.minecraft.core.items.CoreItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

@Getter
@RequiredArgsConstructor
public enum ItemCategory {
    ARMOR_AND_WEAPONS("armor_and_weapons", CoreItem.generate(Material.DIAMOND_SWORD)),
    CONSUMABLES("consumables", CoreItem.generate(Material.APPLE)),
    BLOCKS("blocks", CoreItem.generate(Material.BEDROCK)),
    TOOLS_AND_OTHER("tools_and_other", CoreItem.generate(Material.STICK));

    private final String propertiesKey;
    private final CoreItem display;
}
