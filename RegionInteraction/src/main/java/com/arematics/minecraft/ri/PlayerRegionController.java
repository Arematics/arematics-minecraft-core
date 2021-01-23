package com.arematics.minecraft.ri;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.ri.events.RegionEnterEvent;
import com.arematics.minecraft.ri.events.RegionEnteredEvent;
import com.arematics.minecraft.ri.events.RegionLeaveEvent;
import com.arematics.minecraft.ri.events.RegionLeftEvent;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.springframework.stereotype.Controller;

import java.util.HashSet;
import java.util.Set;

@Getter
@Controller
@NoArgsConstructor
public class PlayerRegionController {

    private final WorldGuardPlugin worldGuard = WorldGuardPlugin.inst();
    private final RegionInteractionBoot plugin = Boots.getBoot(RegionInteractionBoot.class);

    public synchronized boolean updateRegions(final CorePlayer player, final MovementWay movement, Location to) {
        Set<ProtectedRegion> oldRegions = new HashSet<>(player.getCurrentRegions());
        RegionManager rm = this.worldGuard.getRegionManager(to.getWorld());
        if (rm != null) {
            Set<ProtectedRegion> appRegions = rm.getApplicableRegions(to).getRegions();
            ProtectedRegion globalRegion = rm.getRegion("__global__");
            if (globalRegion != null) {
                appRegions.add(globalRegion);
            }

            for(ProtectedRegion region : appRegions) {
                if (!player.getCurrentRegions().contains(region)) {
                    RegionEnterEvent enterEvent = new RegionEnterEvent(region, movement, player);
                    this.plugin.getServer().getPluginManager().callEvent(enterEvent);
                    if (enterEvent.isCancelled()) return cleanUP(player, oldRegions);

                    Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                        RegionEnteredEvent enteredEvent = new RegionEnteredEvent(region, movement, player);
                        this.plugin.getServer().getPluginManager().callEvent(enteredEvent);
                    }, 1L);
                    player.getCurrentRegions().add(region);
                }
            }

            Set<ProtectedRegion> removeRegions = new HashSet<>();

            for(ProtectedRegion region : player.getCurrentRegions()) {
                if (!appRegions.contains(region)) {
                    RegionLeaveEvent leaveEvent = new RegionLeaveEvent(region, movement, player);
                    this.plugin.getServer().getPluginManager().callEvent(leaveEvent);
                    if (leaveEvent.isCancelled()) return cleanUP(player, oldRegions);

                    Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                        RegionLeftEvent leftEvent = new RegionLeftEvent(region, movement, player);
                        Boots.getBoot(RegionInteractionBoot.class).callEvent(leftEvent);
                    }, 1L);
                    removeRegions.add(region);
                }
            }

            player.getCurrentRegions().removeAll(removeRegions);
            return false;
        }
        return false;
    }

    private synchronized boolean cleanUP(CorePlayer player, Set<ProtectedRegion> oldRegions){
        player.getCurrentRegions().clear();
        player.getCurrentRegions().addAll(oldRegions);
        return true;
    }
}
