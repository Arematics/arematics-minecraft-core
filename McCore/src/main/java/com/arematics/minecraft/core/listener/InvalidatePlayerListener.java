package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.Server;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class InvalidatePlayerListener implements Listener {

    private final Server server;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event){
        server.schedule().runAsync(() -> server.players().invalidate(event.getPlayer()));
    }
}
