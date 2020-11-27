package com.arematics.minecraft.crystals.logic;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.data.mode.model.CrystalKey;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CrystalKeyItem {

    public static CoreItem fromKey(CrystalKey key){
        CoreItem item = CoreItem.create(new ItemStack(Material.EYE_OF_ENDER));
        item.setString("crystal", key.getName());
        return item.setName("§7Magic Crystal §8(" + key.getTotalName() + "§8)").setGlow();
    }
}
