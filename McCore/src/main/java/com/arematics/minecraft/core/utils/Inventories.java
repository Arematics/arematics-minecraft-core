package com.arematics.minecraft.core.utils;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class Inventories {

    public static boolean isType(Inventory inventory, InventoryType type){
        return inventory != null && inventory.getType() == type;
    }

    public static void openLowerDisabledInventory(Inventory inventory, CorePlayer player){
        ArematicsExecutor.syncRun(() -> {
            player.getPlayer().playSound(player.getLocation(), Sound.CHEST_OPEN, 5, 5);
            if(!player.disableLowerInventory()) player.disableLowerInventory(true);
            player.getPlayer().openInventory(inventory);
        });
    }

    public static void openTotalBlockedInventory(Inventory inventory, CorePlayer player){
        ArematicsExecutor.syncRun(() -> {
            player.getPlayer().playSound(player.getLocation(), Sound.CHEST_OPEN, 5, 5);
            if(player.getPlayer().getOpenInventory() != null) player.getPlayer().closeInventory();
            if(!player.disableUpperInventory()) player.disableUpperInventory(true);
            if(!player.disableLowerInventory()) player.disableLowerInventory(true);
            player.getPlayer().openInventory(inventory);
        });
    }

    public static void openInventory(Inventory inventory, CorePlayer player){
        player.getPlayer().playSound(player.getLocation(), Sound.CHEST_OPEN, 5, 5);
        ArematicsExecutor.syncRun(() -> player.getPlayer().openInventory(inventory));
    }
}
