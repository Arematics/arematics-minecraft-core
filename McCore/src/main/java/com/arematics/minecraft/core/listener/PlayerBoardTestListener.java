package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.scoreboard.functions.Boards;
import com.arematics.minecraft.core.scoreboard.model.Board;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerBoardTestListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        Board board = Boards.getSet(player).getOrAddBoard("main", "Soul");
        board.addEntry("test", "Test: §7", 0, "§6", "" + "Hallo");
        board.show();
    }
}
