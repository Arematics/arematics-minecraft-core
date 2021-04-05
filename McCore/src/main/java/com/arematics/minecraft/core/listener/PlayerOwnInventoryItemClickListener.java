package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.springframework.stereotype.Component;

@Component
public class PlayerOwnInventoryItemClickListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event){
        CorePlayer player = CorePlayer.get(event.getWhoClicked());
        if(player.inventories().getOwnInvClick() != null
                && event.getClickedInventory() != null
                && event.getClickedInventory().equals(player.getPlayer().getInventory())
                && event.getCurrentItem() != null
                && event.getCurrentItem().getType() != Material.AIR){
            event.setCurrentItem(player.inventories().getOwnInvClick().apply(CoreItem.create(event.getCurrentItem())));
        }
    }
}
