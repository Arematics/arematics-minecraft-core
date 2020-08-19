package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.scoreboard.functions.Boards;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDisconnectBoardListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        Boards.getBoardSet(player).remove();
    }
}
