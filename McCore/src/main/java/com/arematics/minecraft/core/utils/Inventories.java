package com.arematics.minecraft.core.utils;

import com.arematics.minecraft.core.server.CorePlayer;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class Inventories {

    public static boolean isType(Inventory inventory, InventoryType type){
        return inventory != null && inventory.getType() == type;
    }

    public static void openLowerDisabledInventory(Inventory inventory, CorePlayer player){
        ArematicsExecutor.syncRun(() -> {
            player.setDisableLowerInventory(true);
            player.getPlayer().openInventory(inventory);
        });
    }

    public static void openTotalBlockedInventory(Inventory inventory, CorePlayer player){
        ArematicsExecutor.syncRun(() -> {
            player.setDisableUpperInventory(true);
            player.setDisableLowerInventory(true);
            player.getPlayer().openInventory(inventory);
        });
    }

    public static void openInventory(Inventory inventory, CorePlayer player){
        ArematicsExecutor.syncRun(() -> player.getPlayer().openInventory(inventory));
    }
}
