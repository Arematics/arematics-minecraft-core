package com.arematics.minecraft.core.utils;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Inventories {

    public static boolean isType(Inventory inventory, InventoryType type){
        return inventory != null && inventory.getType() == type;
    }

    public static void openLowerDisabledInventory(Inventory inventory, CorePlayer player){
        ArematicsExecutor.syncRun(() -> {
            player.getPlayer().playSound(player.getLocation(), Sound.CHEST_OPEN, 5, 5);
            if(!player.isDisableLowerInventory()) player.setDisableLowerInventory(true);
            player.getPlayer().openInventory(inventory);
        });
    }

    public static void openTotalBlockedInventory(Inventory inventory, CorePlayer player){
        ArematicsExecutor.syncRun(() -> {
            player.getPlayer().playSound(player.getLocation(), Sound.CHEST_OPEN, 5, 5);
            if(player.getPlayer().getOpenInventory() != null) player.getPlayer().closeInventory();
            if(!player.isDisableUpperInventory()) player.setDisableUpperInventory(true);
            if(!player.isDisableLowerInventory()) player.setDisableLowerInventory(true);
            player.getPlayer().openInventory(inventory);
        });
    }

    public static void openInventory(Inventory inventory, CorePlayer player){
        player.getPlayer().playSound(player.getLocation(), Sound.CHEST_OPEN, 5, 5);
        ArematicsExecutor.syncRun(() -> player.getPlayer().openInventory(inventory));
    }

    public static List<Integer> rangeX(int begin, int end){
        if(end < begin) return new ArrayList<>();
        return IntStream.range(begin, end + 1).boxed().collect(Collectors.toList());
    }
}
