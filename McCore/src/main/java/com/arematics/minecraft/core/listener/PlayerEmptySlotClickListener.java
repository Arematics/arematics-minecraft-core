package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.springframework.stereotype.Component;

@Component
public class PlayerEmptySlotClickListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event){
        CorePlayer player = CorePlayer.get(event.getWhoClicked());
        if(player.inventories().getEmptySlotClick() != null
                && event.getClickedInventory() != null
                && player.inventories().getView().getTopInventory().equals(event.getClickedInventory())
                && event.getCurrentItem().getType() == Material.AIR){
            player.inventories().getEmptySlotClick().accept(event.getClickedInventory());
        }
    }
}
