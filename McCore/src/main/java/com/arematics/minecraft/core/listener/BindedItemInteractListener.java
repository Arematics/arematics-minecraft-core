package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.items.Items;
import com.arematics.minecraft.core.server.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.springframework.stereotype.Component;

@Component
public class BindedItemInteractListener implements Listener {

    @EventHandler
    public void executeOnInteract(PlayerInteractEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
            CoreItem item = player.getItemInHand();
            if(item != null && item.hasBindedCommand() && !player.isIgnoreMeta()){
                Bukkit.getServer().dispatchCommand(player.getPlayer(), item.getBindedCommand());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void executeOnInteract(InventoryClickEvent event){
        CorePlayer player = CorePlayer.get((Player)event.getWhoClicked());
        if(event.getCurrentItem() != null && event.getCurrentItem().equals(Items.PLAYERHOLDER)){
            event.setCancelled(true);
            return;
        }
        CoreItem clicked = CoreItem.create(event.getCurrentItem());
        if(clicked != null && clicked.hasBindedCommand() && !player.isIgnoreMeta()){
            Bukkit.getServer().dispatchCommand(player.getPlayer(), clicked.getBindedCommand());
            event.setCancelled(true);
        }
    }
}
