package com.arematics.minecraft.core.server.items;

import com.arematics.minecraft.core.items.CoreItem;
import org.bukkit.Material;
import org.springframework.stereotype.Component;

@Component
public class CoreItemCategoryModifier implements CoreItemCreationModifier{

    @Override
    public CoreItem modify(CoreItem start) {
        if(start.isArmor() || start.isWeapon()) return start.setString("category", ItemCategory.ARMOR_AND_WEAPONS.toString());
        if(start.getType().isEdible() ||
                start.getType() == Material.POTION ||
                start.getType() == Material.ENCHANTED_BOOK ||
                start.getType() == Material.BOOK)
            return start.setString("category", ItemCategory.CONSUMABLES.toString());
        if(start.getType().isBlock()) return start.setString("category", ItemCategory.BLOCKS.toString());
        return start.setString("category", ItemCategory.TOOLS_AND_OTHER.toString());
    }
}
