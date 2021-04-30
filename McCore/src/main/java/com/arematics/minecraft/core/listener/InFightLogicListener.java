package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.springframework.stereotype.Component;

@Component
public class InFightLogicListener implements Listener {

    @EventHandler
    public void inFight(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player && event.getDamager() instanceof Player){
            CorePlayer damagePlayer = CorePlayer.get((Player) event.getEntity());
            CorePlayer damagedPlayer = CorePlayer.get((Player) event.getDamager());
            RegionQuery query = WorldGuardPlugin.inst().getRegionContainer().createQuery();
            if(damagePlayer.regions().isFlagEnabled(query, DefaultFlag.PVP) &&
                    damagedPlayer.regions().isFlagEnabled(query, DefaultFlag.PVP)) {
                if (!damagePlayer.equals(damagedPlayer)) {
                    if (!damagePlayer.interact().inFight()) damagePlayer.warn("In fight, dont log out").handle();
                    if (!damagedPlayer.interact().inFight()) damagedPlayer.warn("In fight, dont log out").handle();
                    damagePlayer.interact().setInFight();
                    damagedPlayer.interact().setInFight();
                }
            }
        }
    }
}
