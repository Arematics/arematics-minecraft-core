package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.utils.Inventories;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class AnvilDragBlockListener implements Listener {


    @EventHandler
    public void onAnvilDragDisabler(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY &&
                Inventories.isType(player.getOpenInventory().getTopInventory(), InventoryType.ANVIL)) {
            e.setCancelled(true);
            Messages.create("anvil_drag_disabled")
                    .WARNING()
                    .to(player)
                    .handle();
        }
    }
}
