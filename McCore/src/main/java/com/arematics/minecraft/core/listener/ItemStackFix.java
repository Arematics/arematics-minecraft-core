package com.arematics.minecraft.core.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.springframework.stereotype.Component;

@Component
public class ItemStackFix implements Listener {

    @EventHandler
    public void onItemPickUp(PlayerPickupItemEvent event) {

        if (event.isCancelled()) {
            return;
        }

        if (event.getItem().getItemStack().getAmount() > event.getItem().getItemStack().getMaxStackSize()) {
            event.setCancelled(true);
            ItemStack stack = event.getItem().getItemStack();

            event.getItem().remove();
            event.getPlayer().getInventory().addItem(stack);
        }

    }




}
