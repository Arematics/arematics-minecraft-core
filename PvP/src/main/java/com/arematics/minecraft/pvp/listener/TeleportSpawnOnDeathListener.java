package com.arematics.minecraft.pvp.listener;

import com.arematics.minecraft.core.commands.SpawnCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class TeleportSpawnOnDeathListener implements Listener {

    private final SpawnCommand spawnCommand;

    @EventHandler
    public void onDeath(PlayerRespawnEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        player.interact().instantTeleport(spawnCommand.getWarpService().getWarp("spawn").getLocation()).schedule();
    }
}
