package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.utils.Inventories;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.springframework.stereotype.Component;

@Component
public class AnvilDragBlockListener implements Listener {

    @EventHandler
    public void onAnvilDragDisabler(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();
        if (clickEvent.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY &&
                player.getOpenInventory().getTopInventory() != null &&
                Inventories.isType(player.getOpenInventory().getTopInventory(), InventoryType.ANVIL)) {
            clickEvent.setCancelled(true);
            Messages.create("anvil_drag_disabled").WARNING().to(player).handle();
        }
    }
}
