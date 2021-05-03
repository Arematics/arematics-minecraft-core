package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.bukkit.wrapper.AsyncPlayerDeathEvent;
import com.arematics.minecraft.core.server.Server;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class PlayerDeathEventWrapper implements Listener {

    private final Server server;

    @EventHandler
    public void onDeathEvent(org.bukkit.event.entity.PlayerDeathEvent event){
        server.schedule().runAsync(() -> callDeath(event));
    }

    private void callDeath(PlayerDeathEvent event){
        AsyncPlayerDeathEvent deathEvent = new AsyncPlayerDeathEvent(server.fetchPlayer(event.getEntity()),
                server.fetchPlayer(event.getEntity().getKiller()),
                event.getDrops(),
                event.getDroppedExp(),
                event.getNewExp(),
                event.getNewTotalExp(),
                event.getNewLevel(),
                event.getDeathMessage());
        Bukkit.getServer().getPluginManager().callEvent(deathEvent);
    }
}
