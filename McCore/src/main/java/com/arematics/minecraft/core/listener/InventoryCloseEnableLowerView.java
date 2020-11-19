package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.CorePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.springframework.stereotype.Component;

@Component
public class InventoryCloseEnableLowerView implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent event){
        CorePlayer player = CorePlayer.get((Player) event.getPlayer());
        player.setDisableLowerInventory(false);
    }
}
