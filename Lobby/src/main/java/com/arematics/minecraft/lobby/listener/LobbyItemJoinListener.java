package com.arematics.minecraft.lobby.listener;

import com.arematics.minecraft.core.bukkit.scoreboard.functions.BoardHandler;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.service.WarpService;
import com.arematics.minecraft.lobby.items.Items;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class LobbyItemJoinListener implements Listener {

    private final WarpService warpService;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        player.interact().instantTeleport(warpService.getWarp("spawn").getLocation()).schedule();
        player.getPlayer().getInventory().setItem(4, Items.COMPASS);
        sendScoreboard(player);
    }

    private void sendScoreboard(CorePlayer player){
        final BoardHandler handler = player.getBoard().getOrAddBoard("main", "§b§lSOULPVP.DE");
        handler.addEntryData("Welcome", "§c", "§bon SoulPvP.de");

        ArematicsExecutor.syncRun(handler::show);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        if(event.getTo().getX() >= 2000 || event.getTo().getZ() >= 2000 || event.getTo().getX() <= -2000 || event.getTo().getZ() <= -2000){
            player.interact().instantTeleport(warpService.getWarp("spawn").getLocation()).schedule();
        }
    }
}
