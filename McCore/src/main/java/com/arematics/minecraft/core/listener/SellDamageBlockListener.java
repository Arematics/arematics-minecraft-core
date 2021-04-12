package com.arematics.minecraft.core.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.springframework.stereotype.Component;

@Component
public class SellDamageBlockListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {

        final Entity entity = event.getEntity();

        if (entity instanceof Player) {
            final Entity damager = event.getDamager();

            if (damager instanceof Player && entity == damager) event.setCancelled(true);
        }
    }
}
