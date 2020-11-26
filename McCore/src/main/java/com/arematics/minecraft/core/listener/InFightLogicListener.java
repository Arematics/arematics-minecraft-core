package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.CorePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.springframework.stereotype.Component;

@Component
public class InFightLogicListener implements Listener {

    @EventHandler
    public void inFight(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player && event.getDamager() instanceof  Player){
            CorePlayer damagePlayer = CorePlayer.get((Player) event.getEntity());
            CorePlayer damagedPlayer = CorePlayer.get((Player) event.getDamager());
            if(!damagePlayer.equals(damagedPlayer)){
                damagePlayer.setInFight();
                damagedPlayer.setInFight();
                damagePlayer.warn("In fight, dont log out").handle();
                damagedPlayer.warn("In fight, dont log out").handle();
            }
        }
    }
}
