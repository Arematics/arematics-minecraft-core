package com.arematics.minecraft.core.items;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public class ItemUpdateClickListener implements Listener {

    private CoreItem item;
    private final ClickType clickType;
    private final Function<CoreItem, CoreItem> action;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClick(InventoryClickEvent event){
        CorePlayer player = CorePlayer.get(event.getWhoClicked());
        if(event.getClickedInventory() != null && event.getCurrentItem() != null && item.isSimilar(event.getCurrentItem()) && (clickType == null || event.getClick() == clickType)){
            ArematicsExecutor.syncDelayed(() -> {
                CoreItem result = item = action.apply(item);
                event.getClickedInventory().setItem(event.getSlot(), result);
                player.getPlayer().updateInventory();
            }, 300, TimeUnit.MILLISECONDS);
        }
    }
}
