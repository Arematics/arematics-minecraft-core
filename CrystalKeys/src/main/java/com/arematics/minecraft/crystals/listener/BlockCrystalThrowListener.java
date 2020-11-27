package com.arematics.minecraft.crystals.listener;

import com.arematics.minecraft.core.items.CoreItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.springframework.stereotype.Component;

@Component
public class BlockCrystalThrowListener implements Listener {

    @EventHandler
    public void onBlockCrystalThrow(PlayerInteractEvent event){
        CoreItem item = CoreItem.create(event.getPlayer().getItemInHand());
        if(item != null && item.getMeta().hasKey("crystal")) event.setCancelled(true);
    }
}
