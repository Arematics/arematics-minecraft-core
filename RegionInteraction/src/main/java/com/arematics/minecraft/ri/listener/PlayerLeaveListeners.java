package com.arematics.minecraft.ri.listener;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.ri.MovementWay;
import com.arematics.minecraft.ri.RegionInteractionBoot;
import com.arematics.minecraft.ri.events.RegionLeftEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PlayerLeaveListeners implements Listener {

    private final RegionInteractionBoot plugin;

    public PlayerLeaveListeners(){
        this.plugin = Boots.getBoot(RegionInteractionBoot.class);
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        removePlayerFromRegions(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removePlayerFromRegions(event.getPlayer());
    }

    private void removePlayerFromRegions(Player eventPlayer){
        CorePlayer player = CorePlayer.get(eventPlayer);
        Set<ProtectedRegion> regions =  player.regions().getCurrentRegions();
        player.regions().getCurrentRegions().clear();
        if (regions != null) {
            regions.forEach(region -> callLeftEvent(player, region));
        }
    }

    private void callLeftEvent(CorePlayer player, ProtectedRegion region){
        RegionLeftEvent leftEvent = new RegionLeftEvent(region, MovementWay.DISCONNECT, player);
        this.plugin.callEvent(leftEvent);
    }
}
