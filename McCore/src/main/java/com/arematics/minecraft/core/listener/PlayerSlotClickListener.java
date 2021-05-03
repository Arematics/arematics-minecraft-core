package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class PlayerSlotClickListener implements Listener {

    private final Server server;

    @EventHandler
    public void onClick(InventoryClickEvent event){
        CorePlayer player = server.fetchPlayer((Player) event.getWhoClicked());
        if(player.inventories().getSlotClick() != null
                && player.inventories().getSlots() != null
                && event.getClickedInventory() != null
                && event.getCurrentItem() != null
                && event.getCurrentItem().getType() != Material.AIR
                && player.inventories().getView().getTopInventory().equals(event.getClickedInventory())
                && player.inventories().getSlots().toList().contains(event.getSlot())){
            server.schedule().syncDelayed(() -> player.inventories().getSlotClick().accept(event.getClickedInventory(),
                    server.items().createNoModifier(event.getCurrentItem())),
                    250,
                    TimeUnit.MILLISECONDS);
        }
    }
}
