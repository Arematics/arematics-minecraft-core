package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.items.MetaClickExecutorsCache;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BindedItemInteractListener implements Listener {

    private final MetaClickExecutorsCache executorsCache;

    @Autowired
    public BindedItemInteractListener(MetaClickExecutorsCache metaClickExecutorsCache){
        this.executorsCache = metaClickExecutorsCache;
    }

    @EventHandler
    public void executeOnInteract(PlayerInteractEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
            CoreItem item = player.getItemInHand();
            if(!player.isIgnoreMeta())
                event.setCancelled(this.executorsCache.searchAndRun(player, item));
        }
    }

    @EventHandler
    public void executeOnInteract(InventoryClickEvent event){
        CorePlayer player = CorePlayer.get((Player)event.getWhoClicked());
        CoreItem clicked = CoreItem.create(event.getCurrentItem());
        if(clicked == null) return;
        if(!player.isIgnoreMeta())
            event.setCancelled(this.executorsCache.searchAndRun(player, clicked));
        if(!player.isIgnoreMeta() && event.getClickedInventory() != null &&
                event.getClickedInventory().equals(player.inventories().getView().getBottomInventory()) &&
                player.isDisableLowerInventory()){
            event.setCancelled(true);
            return;
        }
        if(!player.isIgnoreMeta() && event.getClickedInventory() != null &&
                event.getClickedInventory().equals(player.inventories().getView().getTopInventory()) &&
                player.isDisableUpperInventory()){
            event.setCancelled(true);
        }
    }
}
