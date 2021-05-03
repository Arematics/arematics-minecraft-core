package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.server.entities.player.inventories.InventoryController;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class AntiStackedItemsListener implements Listener {

    private final InventoryController inventories;

    @EventHandler
    public void onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        if (event.getCursor() != null &&
                player.getOpenInventory().getTopInventory() != null &&
                inventories.isType(player.getOpenInventory().getTopInventory(), InventoryType.ANVIL)) {
            event.setCancelled(true);
            Messages.create("anvil_drag_disabled").WARNING().to(player).handle();
        }
    }
}
