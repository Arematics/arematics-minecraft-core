package com.arematics.minecraft.guns.listener;

import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.springframework.stereotype.Component;

@Component
public class PlayerHitBySnowballListener implements Listener {

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player){
            if(event.getDamager() instanceof Snowball){
                System.out.println("HIT YOU BITCH");
            }
        }
    }
}
