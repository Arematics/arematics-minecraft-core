package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.CorePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.springframework.stereotype.Component;

@Component
public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onKill(PlayerDeathEvent event){
        CorePlayer killed = CorePlayer.get(event.getEntity());
        CorePlayer killer = CorePlayer.get(event.getEntity().getKiller());
        killed.addDeath();
        killer.addKill();
    }
}
