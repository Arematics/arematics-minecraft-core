package com.arematics.minecraft.core.server.entities.player.inventories.helper;

import com.arematics.minecraft.core.items.CoreItem;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.stream.IntStream;

public class InventoryPlaceholder {

    public static void fillInventory(Inventory inv, DyeColor color){
        CoreItem glassPane = CoreItem.create(new ItemStack(Material.STAINED_GLASS_PANE, 1, color.getData()));
        glassPane.disableClick().setName("§8 ");
        IntStream.range(0, inv.getSize()).forEach(index -> inv.setItem(index, glassPane));
    }

    public static void fillOuterLine(Inventory inv, DyeColor color){
        CoreItem glassPane = CoreItem.create(new ItemStack(Material.STAINED_GLASS_PANE, 1, color.getData()));
        glassPane.disableClick().setName("§8 ");
        IntStream.range(0, 9).forEach(index -> inv.setItem(index, glassPane));
        IntStream.range(inv.getSize() - 9, inv.getSize()).forEach(index -> inv.setItem(index, glassPane));
        int rows = inv.getSize() / 9;
        IntStream.range(1, rows).forEach(row -> {
            inv.setItem(row * 9, glassPane);
            inv.setItem(row * 9 + 8, glassPane);
        });
    }

    public static void fillFree(Inventory inv, ItemStack itemStack){
        int empty;
        while((empty = inv.firstEmpty()) != -1) inv.setItem(empty, itemStack);
    }

    public static void clear(Inventory inv, IntegerBox box){
        box.toList().forEach(index -> inv.setItem(index, null));
    }

}
