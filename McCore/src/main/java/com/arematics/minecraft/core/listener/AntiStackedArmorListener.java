package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.messaging.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.springframework.stereotype.Component;

@Component
public class AntiStackedArmorListener implements Listener {

    @EventHandler
    public void onInteract(InventoryClickEvent event){
        Player player = (Player)event.getWhoClicked();

        if(event.getSlotType() == InventoryType.SlotType.ARMOR){
            if(event.getCursor() != null && event.getCursor().getAmount() > 1){
                Messages.create("armor_max_stack_size").WARNING().to(player).handle();
                event.setCancelled(true);
            }
        }
    }
}
