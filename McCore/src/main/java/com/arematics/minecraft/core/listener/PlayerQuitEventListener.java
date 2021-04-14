package com.arematics.minecraft.core.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class PlayerQuitEventListener implements Listener {

    private final Set<Player> blocked = new HashSet<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event){
        blocked.add(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuitEnd(PlayerQuitEvent event){
        blocked.remove(event.getPlayer());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        if(blocked.contains(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event){
        if(blocked.contains(event.getPlayer())) event.setCancelled(true);
    }
}
