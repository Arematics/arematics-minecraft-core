package com.arematics.minecraft.ri;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.ri.events.RegionEnterEvent;
import com.arematics.minecraft.ri.events.RegionEnteredEvent;
import com.arematics.minecraft.ri.events.RegionLeaveEvent;
import com.arematics.minecraft.ri.events.RegionLeftEvent;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@Controller
@NoArgsConstructor
public class PlayerRegionController {

    private final WorldGuardPlugin worldGuard = WorldGuardPlugin.inst();
    private final RegionInteractionBoot plugin = Boots.getBoot(RegionInteractionBoot.class);

    public synchronized boolean updateRegions(final CorePlayer player, final MovementWay movement, Location to) {
        Set<ProtectedRegion> regions = player.getCurrentRegions();
        Set<ProtectedRegion> oldRegions = new HashSet<>(regions);
        RegionManager rm = this.worldGuard.getRegionManager(to.getWorld());
        if (rm != null) {
            ApplicableRegionSet appRegions = rm.getApplicableRegions(to);

            for(ProtectedRegion region : appRegions) {
                if (!regions.contains(region)) {
                    RegionEnterEvent enterEvent = new RegionEnterEvent(region, movement, player);
                    this.plugin.getServer().getPluginManager().callEvent(enterEvent);
                    if (enterEvent.isCancelled()) return cleanUP(regions, oldRegions);

                    Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                        RegionEnteredEvent enteredEvent = new RegionEnteredEvent(region, movement, player);
                        Boots.getBoot(RegionInteractionBoot.class).callEvent(enteredEvent);
                    }, 1L);
                    regions.add(region);
                }
            }

            Collection<ProtectedRegion> app = appRegions.getRegions();

            for(ProtectedRegion region : app) {
                if (!regions.contains(region)) {
                    RegionLeaveEvent leaveEvent = new RegionLeaveEvent(region, movement, player);
                    this.plugin.getServer().getPluginManager().callEvent(leaveEvent);
                    if (leaveEvent.isCancelled()) return cleanUP(regions, oldRegions);

                    Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                        RegionLeftEvent leftEvent = new RegionLeftEvent(region, movement, player);
                        Boots.getBoot(RegionInteractionBoot.class).callEvent(leftEvent);
                    }, 1L);
                    regions.add(region);
                }
            }

            player.getCurrentRegions().clear();
            player.getCurrentRegions().addAll(regions);
        }
        return false;
    }

    private boolean cleanUP(Set<ProtectedRegion> regions, Set<ProtectedRegion> oldRegions){
        regions.clear();
        regions.addAll(oldRegions);
        return true;
    }
}
