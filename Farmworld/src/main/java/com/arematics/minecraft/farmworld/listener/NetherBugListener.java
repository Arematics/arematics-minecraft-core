package com.arematics.minecraft.farmworld.listener;

import com.arematics.minecraft.core.commands.SpawnCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.mode.model.Warp;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class NetherBugListener implements Listener {

    private final SpawnCommand spawnCommand;

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Location to = event.getTo();
        if(to.getWorld().getEnvironment() == World.Environment.NETHER || to.getBlockY() >= 127){
            ArematicsExecutor.runAsync(() -> {
                Warp warp = spawnCommand.getWarpService().getWarp(spawnCommand.getCurrentTeleport());
                CorePlayer player = CorePlayer.get(event.getPlayer());
                player.instantTeleport(warp.getLocation());
                player.warn("Bug about the nether is not allowed").handle();
            });
        }
    }
}
