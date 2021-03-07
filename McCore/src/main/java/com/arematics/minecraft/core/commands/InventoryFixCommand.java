package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class InventoryFixCommand extends CoreCommand {

    ArrayList<Material> forbiddenStackItems = new ArrayList<>();

    public InventoryFixCommand() {
        super("ifix");
        forbiddenStackItems.add(Material.POTION);
        forbiddenStackItems.add(Material.GOLDEN_APPLE);
        forbiddenStackItems.add(Material.SKULL);
        forbiddenStackItems.add(Material.SKULL_ITEM);
        forbiddenStackItems.add(Material.LEAVES);
        forbiddenStackItems.add(Material.LEAVES_2);
        forbiddenStackItems.add(Material.SAPLING);
        forbiddenStackItems.add(Material.AIR);
        forbiddenStackItems.add(Material.MONSTER_EGGS);
        forbiddenStackItems.add(Material.MONSTER_EGG);
        forbiddenStackItems.add(Material.DIAMOND_PICKAXE);
        forbiddenStackItems.add(Material.IRON_PICKAXE);
        forbiddenStackItems.add(Material.WOOD_PICKAXE);
        forbiddenStackItems.add(Material.STONE_PICKAXE);
        forbiddenStackItems.add(Material.GOLD_PICKAXE);
        forbiddenStackItems.add(Material.DIAMOND_AXE);
        forbiddenStackItems.add(Material.IRON_AXE);
        forbiddenStackItems.add(Material.WOOD_AXE);
        forbiddenStackItems.add(Material.STONE_AXE);
        forbiddenStackItems.add(Material.GOLD_AXE);
        forbiddenStackItems.add(Material.DIAMOND_SPADE);
        forbiddenStackItems.add(Material.IRON_SPADE);
        forbiddenStackItems.add(Material.WOOD_SPADE);
        forbiddenStackItems.add(Material.STONE_SPADE);
        forbiddenStackItems.add(Material.GOLD_SPADE);


    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        for (ItemStack current : sender.getPlayer().getInventory().getContents())
            if (current != null && !forbiddenStackItems.contains(current.getType()) && !current.getType().isBlock())
                current.setDurability((short) 0);


        sender.info("You have repaired your inventory items").handle();
    }
}
