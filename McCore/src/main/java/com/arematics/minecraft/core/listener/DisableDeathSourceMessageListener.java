package com.arematics.minecraft.core.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.springframework.stereotype.Component;

@Component
public class DisableDeathSourceMessageListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        event.setDeathMessage("");
    }
}
