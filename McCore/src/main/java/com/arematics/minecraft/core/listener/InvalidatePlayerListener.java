package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.CorePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.springframework.stereotype.Component;

@Component
public class InvalidatePlayerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event){
        CorePlayer.invalidate(event.getPlayer());
        System.out.println("Callya Later");
    }
}
