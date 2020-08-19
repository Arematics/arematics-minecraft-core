package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.CoreEngine;
import com.arematics.minecraft.core.Engine;
import com.arematics.minecraft.core.scoreboard.functions.BoardHandler;
import com.arematics.minecraft.core.scoreboard.functions.Boards;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerBoardTestListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        final BoardHandler handler = Boards.getBoardSet(player).getOrAddBoard("main", "§aSoul");
        handler.addEntryData("Test: §7", "§6", "§4Hallo")
                .show();

        new BukkitRunnable(){
            @Override
            public void run() {
                handler.setEntrySuffix("Test: §7", "§4Allo")
                        .setEntrySuffix("Test: §7", "§5Ein Zebra")
                        .buildEntries();
            }
        }.runTaskLater(Engine.getEngine(CoreEngine.class).getBootstrap(), 20*5);
    }
}