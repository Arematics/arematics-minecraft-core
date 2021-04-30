package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.springframework.stereotype.Component;

@Component
public class DamagePotInNoPvPAreaListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onDmg(EntityDamageEvent e){
        if(e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.MAGIC){
            CorePlayer player = CorePlayer.get((Player) e.getEntity());
            boolean allow = player.regions().getCurrentRegions()
                    .stream()
                    .anyMatch(region -> region.getId().toLowerCase().contains("allowdamage"));

            if(!allow)
                e.setCancelled(true);
        }
    }

}
