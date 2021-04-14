package com.arematics.minecraft.meta.listener;

import com.arematics.minecraft.meta.events.PrepareAnvilEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class AnvilPrepareListener implements Listener {

    @EventHandler
    public void onAnvil(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if(e.getClickedInventory() == null || e.getCursor() == null || e.getCursor().getType() == Material.AIR)
            return;
        if (e.getClickedInventory().getType() == InventoryType.ANVIL) {
            if(e.getSlotType() == InventoryType.SlotType.CRAFTING) {
                PrepareAnvilEvent event = new PrepareAnvilEvent((AnvilInventory)e.getClickedInventory(),
                        e.getCursor(), player);
                Bukkit.getServer().getPluginManager().callEvent(event);
                e.setCancelled(event.isCancelled());
            }
        }
    }
}
