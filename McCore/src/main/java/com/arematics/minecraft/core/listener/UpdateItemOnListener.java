package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.Server;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class UpdateItemOnListener {
    private final Server server;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDrag(InventoryDragEvent event){
        if(event.getCursor() != null && event.getCursor().getType() != Material.AIR)
            event.setCursor(server.items().create(event.getCursor()));
    }
}
