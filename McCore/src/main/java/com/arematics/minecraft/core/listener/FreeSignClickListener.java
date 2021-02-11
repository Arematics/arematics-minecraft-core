package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.inventories.helper.InventoryPlaceholder;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class FreeSignClickListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null &&
                (event.getClickedBlock().getType() == Material.SIGN || event.getClickedBlock().getType() == Material.WALL_SIGN)){
            Sign clicked = (Sign) event.getClickedBlock().getState();
            if(clicked.getLine(0).equals("§8[§6FREE§8]")){
                Location loc = event.getClickedBlock().getLocation().add(0.5, 1, 0.5);
                Collection<Entity> entities = loc.getWorld().getNearbyEntities(loc, 0.5, 0.5, 0.5);
                entities = entities.stream().filter(entity -> entity instanceof ItemFrame).collect(Collectors.toList());
                if(!entities.isEmpty()){
                    ItemFrame frame = (ItemFrame) entities.stream().findFirst().orElse(null);
                    CoreItem item = CoreItem.create(frame.getItem());
                    item.setAmount(item.getMaxStackSize());
                    Inventory inv = Bukkit.createInventory(null, 5*9, "§6Free");
                    player.openLowerEnabledInventory(inv);
                    InventoryPlaceholder.fillOuterLine(inv, DyeColor.BLACK);
                    InventoryPlaceholder.fillFree(inv, item);
                }
            }
        }
    }
}

