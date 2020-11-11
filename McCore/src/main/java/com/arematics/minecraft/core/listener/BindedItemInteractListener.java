package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.commands.IgnoreMetaCommand;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.items.Items;
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
        Player player = event.getPlayer();
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
            CoreItem item = CoreItem.create(player.getItemInHand());
            if(item != null && item.hasBindedCommand() && !IgnoreMetaCommand.isIgnoreMeta(player)){
                Bukkit.getServer().dispatchCommand(player, item.getBindedCommand());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void executeOnInteract(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        if(event.getCurrentItem().equals(Items.PLAYERHOLDER)){
            event.setCancelled(true);
            return;
        }
        CoreItem clicked = CoreItem.create(event.getCurrentItem());
        if(clicked != null && clicked.hasBindedCommand() && !IgnoreMetaCommand.isIgnoreMeta(player)){
            Bukkit.getServer().dispatchCommand(player, clicked.getBindedCommand());
            event.setCancelled(true);
        }
    }
}
