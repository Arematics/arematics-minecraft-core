package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.utils.Inventories;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.springframework.stereotype.Component;

@Component
public class AntiStackedItemsListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        if (event.getCursor() != null &&
                player.getOpenInventory().getTopInventory() != null &&
                Inventories.isType(player.getOpenInventory().getTopInventory(), InventoryType.ANVIL)) {
            event.setCancelled(true);
            Messages.create("anvil_drag_disabled").WARNING().to(player).handle();
        }
    }
}
