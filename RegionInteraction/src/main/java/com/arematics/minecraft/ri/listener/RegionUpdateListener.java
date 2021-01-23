package com.arematics.minecraft.ri.listener;

import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.ri.MovementWay;
import com.arematics.minecraft.ri.PlayerRegionController;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegionUpdateListener implements Listener {

    private final PlayerRegionController controller;

    @Autowired
    public RegionUpdateListener(PlayerRegionController playerRegionController){
        this.controller = playerRegionController;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        CorePlayer player = CorePlayer.get(event.getPlayer());
        event.setCancelled(this.controller.updateRegions(player, MovementWay.MOVE, event.getTo()));
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        CorePlayer player = CorePlayer.get(event.getPlayer());
        event.setCancelled(this.controller.updateRegions(player, MovementWay.TELEPORT, event.getTo()));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        CorePlayer player = CorePlayer.get(event.getPlayer());
        this.controller.updateRegions(player, MovementWay.SPAWN, event.getPlayer().getLocation());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        CorePlayer player = CorePlayer.get(event.getPlayer());
        this.controller.updateRegions(player, MovementWay.SPAWN, event.getRespawnLocation());
    }
}
