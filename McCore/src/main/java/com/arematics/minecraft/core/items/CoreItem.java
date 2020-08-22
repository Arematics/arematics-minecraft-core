package com.arematics.minecraft.core.items;

import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@ToString
public class CoreItem extends NBTItem {

    public static CoreItem create(ItemStack item){
        if(item == null || item.getType() == Material.AIR) return null;
        return new CoreItem(item);
    }

    private final ItemStack item;

    private CoreItem(ItemStack item){
        super(item);
        this.item = item;
    }
}
