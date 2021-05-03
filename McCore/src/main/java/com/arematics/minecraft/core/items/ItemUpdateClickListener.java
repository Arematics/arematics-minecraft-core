package com.arematics.minecraft.core.items;

import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.InventoryHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public class ItemUpdateClickListener implements Listener {

    private final Server server;
    private CoreItem item;
    private final ClickType clickType;
    private final Function<CoreItem, CoreItem> action;
    private final Inventory inventory;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClick(InventoryClickEvent event){
        CorePlayer player = server.players().fetchPlayer((Player) event.getWhoClicked());
        if(event.getClickedInventory() != null && event.getInventory().equals(inventory)
                && event.getCurrentItem() != null
                && item != null
                && item.isSimilar(event.getCurrentItem())
                && (clickType == null || event.getClick() == clickType)
                && !player.handle(InventoryHandler.class).isClickBlocked()){
            server.schedule().syncDelayed(() -> {
                CoreItem result = item = action.apply(item);
                event.getClickedInventory().setItem(event.getSlot(), result);
                player.getPlayer().updateInventory();
            }, 300, TimeUnit.MILLISECONDS);
        }
    }
}
