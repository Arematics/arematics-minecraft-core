package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.service.InventoryService;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.Inventory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@Perm(permission = "sendto", description = "Send Chest to other inventory")
public class SendToInventoryCommand extends CoreCommand {

    private final InventoryService service;

    @Autowired
    public SendToInventoryCommand(InventoryService inventoryService) {
        super("isendto", true);
        this.service = inventoryService;
    }

    @SubCommand("{inventory}")
    public void sendChestToInventory(CorePlayer player, String name) {
        try{
            Inventory inv = null;
            Block block = player.getPlayer().getTargetBlock((Set<Material>) null, 100);

            if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
                if(block.getState() instanceof DoubleChest) {
                    DoubleChest chest = (DoubleChest) block.getState();
                    CoreItem[] items = CoreItem.create(chest.getInventory().getContents());
                    copyInventory(player, items, inv);
                }else{
                    Chest chest = (Chest) block.getState();
                    CoreItem[] items = CoreItem.create(chest.getInventory().getContents());
                    copyInventory(player, items, inv);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            player.warn("Inventory with name: " + name + " could not be found").handle();
        }
    }

    private void copyInventory(CorePlayer player, CoreItem[] items, Inventory inv){
        if(inv.getSize() >= items.length){
            inv.setContents(items);
            player.info("Chest Content copied to inventory").handle();
        }else
            player.warn("Chest is bigger than target inventory").handle();
    }
}
