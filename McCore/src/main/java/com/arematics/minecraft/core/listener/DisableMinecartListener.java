package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.events.PlayerInteractEvent;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.InteractType;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.Material;
import org.bukkit.entity.minecart.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.springframework.stereotype.Component;

@Component
public class DisableMinecartListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeath(EntityDeathEvent event){
        if(event.getEntity() instanceof StorageMinecart ||
                event.getEntity() instanceof ExplosiveMinecart ||
                event.getEntity() instanceof HopperMinecart ||
                event.getEntity() instanceof CommandMinecart ||
                event.getEntity() instanceof PoweredMinecart){
            event.getEntity().remove();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlace(PlayerInteractEvent event){
        CorePlayer player = event.getPlayer();
        if(event.getType() == InteractType.RIGHT_CLICK && player.interact().getItemInHand() != null){
            CoreItem item = player.interact().getItemInHand();
            if(item.getType() == Material.STORAGE_MINECART
                    || item.getType() == Material.POWERED_MINECART
                    || item.getType() == Material.COMMAND_MINECART
                    || item.getType() == Material.EXPLOSIVE_MINECART
                    || item.getType() == Material.HOPPER_MINECART){
                event.setCancelled(true);
            }
        }
    }
}
