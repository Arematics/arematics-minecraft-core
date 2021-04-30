package com.arematics.minecraft.guns.listener;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.guns.calculation.Ammo;
import com.arematics.minecraft.maps.commands.MapSpawnCommand;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class PlayerTeleportSpawnListener implements Listener {
    private final MapSpawnCommand mapSpawnCommand;
    private final Ammo ammo;

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        player.interact().instantTeleport(mapSpawnCommand.findCurrentSpawn()).schedule();
    }

    @EventHandler
    public void onDeath(PlayerRespawnEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        ammo.stopAim(player);
        player.interact().instantTeleport(mapSpawnCommand.findCurrentSpawn()).schedule();
    }
}
