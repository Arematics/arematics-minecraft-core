package com.arematics.minecraft.guns.listener;

import com.arematics.minecraft.core.bukkit.wrapper.AsyncPlayerDeathEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.stereotype.Component;

@Component
public class CoinsOnKillListener implements Listener {

    @EventHandler
    public void kill(AsyncPlayerDeathEvent event){
        if(event != null) event.getKiller().addMoney(10);
    }
}
