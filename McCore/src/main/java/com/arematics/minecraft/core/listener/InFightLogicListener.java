package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.RegionHandler;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class InFightLogicListener implements Listener {

    private final Server server;

    @EventHandler
    public void inFight(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player && event.getDamager() instanceof Player){
            CorePlayer damagePlayer = server.players().fetchPlayer((Player) event.getEntity());
            CorePlayer damagedPlayer = server.players().fetchPlayer((Player) event.getDamager());
            RegionQuery query = WorldGuardPlugin.inst().getRegionContainer().createQuery();
            if(damagePlayer.handle(RegionHandler.class).isFlagEnabled(query, DefaultFlag.PVP) &&
                    damagedPlayer.handle(RegionHandler.class).isFlagEnabled(query, DefaultFlag.PVP)) {
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
