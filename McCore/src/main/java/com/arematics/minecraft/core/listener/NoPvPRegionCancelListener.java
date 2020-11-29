package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.CorePlayer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.springframework.stereotype.Component;

@Component
public class NoPvPRegionCancelListener implements Listener {

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player){
            CorePlayer player = CorePlayer.get((Player) event.getEntity());
            RegionQuery query = WorldGuardPlugin.inst().getRegionContainer().createQuery();
            if(!player.isFlagEnabled(query, DefaultFlag.PVP)){
                event.setCancelled(true);
                return;
            }
        }
    }
}
