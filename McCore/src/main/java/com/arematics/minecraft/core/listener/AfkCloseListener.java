package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.springframework.stereotype.Component;

@Component
public class AfkCloseListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        ArematicsExecutor.runAsync(() -> asyncUpdate(event));
    }

    private void asyncUpdate(PlayerMoveEvent event){
        Location f = event.getFrom();
        Location t = event.getTo();
        if(f.getBlockX() != t.getBlockX() || f.getBlockY() != t.getBlockY() || f.getBlockZ() != t.getBlockZ()){
            if(!t.getBlock().isLiquid() || t.clone().add(0, -1, 0).getBlock().isLiquid()){
                System.out.println("Closing AFK Time");
                CorePlayer player = CorePlayer.get(event.getPlayer());
                player.callAntiAFK();
            }
        }
    }
}
