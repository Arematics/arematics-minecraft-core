package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class GameModePermissionListener implements Listener {

    private final Server server;

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        server.schedule().asyncDelayed(() -> {
            CorePlayer player = server.fetchPlayer(event.getPlayer());
            if(!player.hasPermission("world.interact.player.gamemode")){
                server.schedule().runSync(() -> player.getPlayer().setGameMode(GameMode.SURVIVAL));
            }
        }, 3, TimeUnit.SECONDS);
    }
}
