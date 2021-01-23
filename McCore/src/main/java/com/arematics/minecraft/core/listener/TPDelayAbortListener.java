package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.springframework.stereotype.Component;

@Component
public class TPDelayAbortListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Location f = e.getFrom();
        Location t = e.getTo();
        if(f.getBlockX() != t.getBlockX() || f.getBlockY() != t.getBlockY() || f.getBlockZ() != t.getBlockZ()){
            CorePlayer player = CorePlayer.get(e.getPlayer());
            player.stopTeleport();
        }
    }
}
