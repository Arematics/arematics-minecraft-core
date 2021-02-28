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
        event.getPlayer().failure("You have been killed by %player%")
                .DEFAULT()
                .replace("player", event.getKiller().getPlayer().getName())
                .handle();
        event.getPlayer().fightEnd();
        if(event.getKiller() != null){
            event.getKiller().addKill();
            event.getKiller().info("You have killed player %player%")
                    .DEFAULT()
                    .replace("player", event.getPlayer().getPlayer().getName())
                    .handle();
        }
    }
}
