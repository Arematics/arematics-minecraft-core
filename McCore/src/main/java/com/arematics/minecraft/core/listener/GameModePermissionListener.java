package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class GameModePermissionListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        ArematicsExecutor.asyncDelayed(() -> {
            CorePlayer player = CorePlayer.get(event.getPlayer());
            if(!player.hasPermission("world.interact.player.gamemode")){
                ArematicsExecutor.syncRun(() -> player.getPlayer().setGameMode(GameMode.SURVIVAL));
            }
        }, 3, TimeUnit.SECONDS);
    }
}
