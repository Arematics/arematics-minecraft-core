package com.arematics.minecraft.guns.listener;

import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.springframework.stereotype.Component;

@Component
public class BulletDeathListener implements Listener {

    @EventHandler
    public void onDeath(ProjectileHitEvent event){
        if(event.getEntity() instanceof Snowball && event.getEntity().getPassenger() != null)
            event.getEntity().getPassenger().remove();
    }
}
