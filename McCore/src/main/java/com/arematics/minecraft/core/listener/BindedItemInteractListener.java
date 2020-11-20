package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.items.CoreItem;
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
                if(item.closeOnClick() && player.getPlayer().getOpenInventory() != null)
                    player.getPlayer().closeInventory();
                Bukkit.getServer().dispatchCommand(player.getPlayer(), item.getBindedCommand());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void executeOnInteract(InventoryClickEvent event){
        CorePlayer player = CorePlayer.get((Player)event.getWhoClicked());
        if(!player.isIgnoreMeta() && event.getClickedInventory() != null &&
                event.getClickedInventory().equals(player.getView().getBottomInventory()) &&
                player.isDisableLowerInventory()){
            event.setCancelled(true);
            return;
        }
        if(!player.isIgnoreMeta() && event.getClickedInventory() != null &&
                event.getClickedInventory().equals(player.getView().getTopInventory()) &&
                player.isDisableUpperInventory()){
            event.setCancelled(true);
            return;
        }
        CoreItem clicked = CoreItem.create(event.getCurrentItem());
        if(clicked == null) return;
        if(clicked.clickDisabled() && !player.isIgnoreMeta()){
            event.setCancelled(true);
            return;
        }
        if(clicked.hasBindedCommand() && !player.isIgnoreMeta()){
            if(clicked.closeOnClick() && player.getPlayer().getOpenInventory() != null)
                player.getPlayer().closeInventory();
            Bukkit.getServer().dispatchCommand(player.getPlayer(), clicked.getBindedCommand());
            event.setCancelled(true);
        }
    }
}
