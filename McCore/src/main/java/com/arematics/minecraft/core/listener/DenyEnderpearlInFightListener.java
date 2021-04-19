package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.events.PlayerInteractEvent;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.stereotype.Component;

@Component
public class DenyEnderpearlInFightListener implements Listener {

    @EventHandler
    public void onThrow(PlayerInteractEvent event){
        CorePlayer player = event.getPlayer();
        CoreItem hand = player.getItemInHand();
        if(hand != null && hand.getType() == Material.ENDER_PEARL){
            event.setCancelled(true);
            player.warn("Using enderpearl's is not allowed");
        }
    }
}
