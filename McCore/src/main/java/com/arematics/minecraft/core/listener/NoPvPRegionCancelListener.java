package com.arematics.minecraft.core.listener;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.springframework.stereotype.Component;

@Component
public class NoPvPRegionCancelListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onHit(EntityDamageByEntityEvent event){
        RegionManager manager = WorldGuardPlugin.inst().getRegionManager(event.getEntity().getWorld());
        ApplicableRegionSet regions = manager.getApplicableRegions(event.getEntity().getLocation());
        if(!regions.testState(null, DefaultFlag.PVP))
            event.setCancelled(true);
    }
}
