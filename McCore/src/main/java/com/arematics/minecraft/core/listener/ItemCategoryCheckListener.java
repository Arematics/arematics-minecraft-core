package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.Server;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class ItemCategoryCheckListener implements Listener {

    private final Server server;

    @EventHandler
    public void onPickup(PlayerPickupItemEvent pickupItemEvent){
        CoreItem item = server.items().create(pickupItemEvent.getItem().getItemStack());
        pickupItemEvent.getItem().setItemStack(item);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent dropItemEvent){
        CoreItem item = server.items().create(dropItemEvent.getItemDrop().getItemStack());
        dropItemEvent.getItemDrop().setItemStack(item);
    }
}
