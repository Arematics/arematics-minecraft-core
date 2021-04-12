package com.arematics.minecraft.guns.listener;

import com.arematics.minecraft.core.bukkit.scoreboard.functions.BoardHandler;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.springframework.stereotype.Component;

@Component
public class ScoreboardOnSpawnListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        ArematicsExecutor.runAsync(() -> sendScoreboard(CorePlayer.get(event.getPlayer())));
    }

    private void sendScoreboard(CorePlayer player){
        final BoardHandler handler = player.getBoard().getOrAddBoard("main", "§b§lSOULPVP.DE");
        handler.addEntryData("Coins", "§c", "§7" + player.stripMoney())
                .addEntryData("Deaths", "§c", "§7" + player.getStats().getDeaths())
                .addEntryData("Kills", "§c", "§7" + player.getStats().getKills());

        ArematicsExecutor.syncRun(handler::show);
    }
}
