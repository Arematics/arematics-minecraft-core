package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.bukkit.wrapper.AsyncPlayerDeathEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.stereotype.Component;

@Component
public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onKill(AsyncPlayerDeathEvent event){
        event.getPlayer().addDeath();
        event.getPlayer().fightEnd();
        event.getKiller().addKill();
        event.getKiller().fightEnd();
    }
}
