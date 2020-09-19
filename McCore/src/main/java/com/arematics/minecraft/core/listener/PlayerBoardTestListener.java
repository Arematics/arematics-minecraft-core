package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.annotations.Permission;
import com.arematics.minecraft.core.data.model.User;
import com.arematics.minecraft.core.data.service.UserService;
import com.arematics.minecraft.core.scoreboard.functions.BoardHandler;
import com.arematics.minecraft.core.scoreboard.functions.Boards;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Timestamp;

public class PlayerBoardTestListener implements Listener{

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        final BoardHandler handler = Boards.getBoardSet(player).getOrAddBoard("main", "§aSoul");
        handler.addEntryData("Test: ", "§6", "§4Hallo").show();

        new BukkitRunnable(){
            @Override
            public void run() {
                handler.setEntryPrefix("Test: ", "§a")
                        .setEntrySuffix("Test: ", "§5Ein Zebra")
                        .refresh();
            }
        }.runTaskLater(Boots.getBoot(CoreBoot.class), 20*5);
    }
}
