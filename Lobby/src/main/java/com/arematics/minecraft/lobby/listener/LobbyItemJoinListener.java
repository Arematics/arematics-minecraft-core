package com.arematics.minecraft.lobby.listener;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.lobby.items.Items;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.springframework.stereotype.Component;

@Component
public class LobbyItemJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        player.instantTeleport(player.getPlayer().getWorld().getSpawnLocation());
        player.getPlayer().getInventory().setItem(4, Items.COMPASS);
    }
}
