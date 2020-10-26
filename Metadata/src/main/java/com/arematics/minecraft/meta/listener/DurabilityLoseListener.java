package com.arematics.minecraft.meta.listener;

import com.arematics.minecraft.meta.events.DurabilityLoseEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class DurabilityLoseListener implements Listener {

    @EventHandler
    public void onHit(PlayerItemDamageEvent event){
        Player player = event.getPlayer();
        int damage = event.getDamage();
        ItemStack itemStack = event.getItem();

        DurabilityLoseEvent durabilityLoseEvent = new DurabilityLoseEvent(itemStack, damage);
        Bukkit.getServer().getPluginManager().callEvent(durabilityLoseEvent);
        event.setCancelled(durabilityLoseEvent.isCancelled());
    }
}
