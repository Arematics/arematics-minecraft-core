package com.arematics.minecraft.core.utils;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class Inventories {

    public static boolean isType(Inventory inventory, InventoryType type){
        return inventory != null && inventory.getType() == type;
    }
}
