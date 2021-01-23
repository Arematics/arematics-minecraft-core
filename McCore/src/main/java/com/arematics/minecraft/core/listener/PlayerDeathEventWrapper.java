package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.bukkit.wrapper.AsyncPlayerDeathEvent;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.springframework.stereotype.Component;

@Component
public class PlayerDeathEventWrapper implements Listener {

    @EventHandler
    public void onDeathEvent(org.bukkit.event.entity.PlayerDeathEvent event){
        ArematicsExecutor.runAsync(() -> callDeath(event));
    }

    private void callDeath(PlayerDeathEvent event){
        AsyncPlayerDeathEvent deathEvent = new AsyncPlayerDeathEvent(CorePlayer.get(event.getEntity()),
                CorePlayer.get(event.getEntity().getKiller()),
                event.getDrops(),
                event.getDroppedExp(),
                event.getNewExp(),
                event.getNewTotalExp(),
                event.getNewLevel(),
                event.getDeathMessage());
        Bukkit.getServer().getPluginManager().callEvent(deathEvent);
    }
}
