package com.arematics.minecraft.core.listener;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.springframework.stereotype.Component;

@Component
public class NullChunkListener implements Listener {

    @EventHandler
    public void onJoin(final PlayerMoveEvent event){
        final Location to = event.getTo();
        final World world = to.getWorld();
        final Chunk chunk = to.getChunk();

        if (chunk == null || !world.isChunkLoaded(chunk)) {
            event.setCancelled(true);
        }
    }
}
