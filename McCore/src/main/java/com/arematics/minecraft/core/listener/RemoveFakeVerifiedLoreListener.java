package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.springframework.stereotype.Component;

@Component
public class RemoveFakeVerifiedLoreListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        CoreItem hand = player.interact().getItemInHand();
        if(removeFalseLore(hand)) player.interact().setItemInHand(hand);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        if(event.getClickedInventory() != null){
            CoreItem clicked = CoreItem.create(event.getCurrentItem());
            if(removeFalseLore(clicked)) event.getClickedInventory().setItem(event.getSlot(), clicked);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        CoreItem clicked = CoreItem.create(event.getItemDrop().getItemStack());
        if(removeFalseLore(clicked)) event.getItemDrop().setItemStack(clicked);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event){
        CoreItem clicked = CoreItem.create(event.getItem().getItemStack());
        if(removeFalseLore(clicked)) event.getItem().setItemStack(clicked);
    }

    private boolean isFalseVerified(CoreItem item){
        return item != null && item.containsLore("Verified") && (!item.getMeta().hasKey("verified") || !item.getMeta().getString("verified").equals("42774"));
    }

    private boolean removeFalseLore(CoreItem item){
        if(isFalseVerified(item)){
            int first;
            while((first = item.findFirst("Verified")) != -1)
                item.removeFromLore(first);
            return true;
        }
        return false;
    }
}
