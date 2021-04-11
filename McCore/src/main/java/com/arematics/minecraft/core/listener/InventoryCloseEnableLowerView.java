package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.springframework.stereotype.Component;

@Component
public class InventoryCloseEnableLowerView implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        player.inventories().getCurrentInventories().forEach(inv -> inv.closeFor(player));
        player.inventories()
                .stopRefreshTask()
                .resetPages()
                .tearDownListeners()
                .setEmptySlotClick(null);
        player.inventories().setSlotClick(null);
        player.inventories().setOwnInvClick(null);
        player.inventories().setSlots(null);
        player.inventories().getCurrentEnums().clear();
        player.inventories().setRefresher(null);
        player.setDisableLowerInventory(false);
        player.setDisableUpperInventory(false);
        player.inventories().getCurrentInventories().clear();
    }
}
