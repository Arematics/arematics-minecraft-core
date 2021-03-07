package com.arematics.minecraft.core.server.entities.player;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.utils.Inventories;
import com.arematics.minecraft.data.service.InventoryService;
import lombok.Data;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

@Data
public class InventoryHandler {

    private static InventoryService inventoryService;

    private final CorePlayer player;
    private int page;
    private boolean resetOnClose;

    InventoryHandler(CorePlayer player){
        this.player = player;
        if(InventoryHandler.inventoryService == null){
            InventoryHandler.inventoryService = Boots.getBoot(CoreBoot.class).getContext().getBean(InventoryService.class);
        }
    }

    public int nextPage(){
        return ++page;
    }

    public int pageBefore(){
        return --page;
    }

    public InventoryView getView(){
        return this.player.getPlayer().getOpenInventory();
    }

    /**
     * Open inventory for player. Own inventory is disabled. Opened inventory is enabled
     * @param inventory Inventory to open
     */
    public void openInventory(Inventory inventory){
        Inventories.openLowerDisabledInventory(inventory, player);
    }

    /**
     * Open inventory for player. Both inventories are blocked
     * @param inventory Inventory to open
     */
    public void openTotalBlockedInventory(Inventory inventory){
        Inventories.openTotalBlockedInventory(inventory, player);
    }
    /**
     * Open inventory for player. Both inventories are enabled
     * @param inventory Inventory to open
     */
    public void openLowerEnabledInventory(Inventory inventory){
        Inventories.openInventory(inventory, player);
    }


    public Inventory getInventory(String key) throws RuntimeException{
        return InventoryHandler.inventoryService.getInventory(player.getUUID() + "." + key);
    }

    public Inventory getOrCreateInventory(String key, String title, byte slots){
        return InventoryHandler.inventoryService.getOrCreate(player.getUUID() + "." + key, title, slots);
    }
}
