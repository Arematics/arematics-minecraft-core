package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class PlayerDisconnectBoardListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        player.getBoardSet().remove();
    }
}
