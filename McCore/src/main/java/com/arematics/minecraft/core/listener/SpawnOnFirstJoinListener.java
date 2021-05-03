package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.commands.SpawnCommand;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.data.mode.model.Warp;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class SpawnOnFirstJoinListener implements Listener {

    private final Server server;
    private final SpawnCommand spawnCommand;

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if(!event.getPlayer().hasPlayedBefore()){
            server.schedule().runAsync(() -> {
                Warp warp = spawnCommand.getWarpService().getWarp(spawnCommand.getCurrentTeleport());
                event.getPlayer().teleport(warp.getLocation());
            });
        }
    }
}
