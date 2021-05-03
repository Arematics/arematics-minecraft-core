package com.arematics.minecraft.core.server.entities.player.inventories;

import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.service.GlobalMessageReceiveService;
import com.arematics.minecraft.data.service.InventoryService;
import com.arematics.minecraft.data.service.ModeMessageReceiveService;
import com.arematics.minecraft.data.share.model.ItemCollection;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.Set;

@Controller
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class InventoryController implements GlobalMessageReceiveService, ModeMessageReceiveService {

    private final Server server;
    private final InventoryService inventoryService;

    private final Map<String, WrappedInventory> wrappedInventoryMap;

    public void open(WrappedInventory inventory, OpenStrategy openStrategy, CorePlayer player){
        inventory.addViewer(player, openStrategy);
        openInventory(inventory.getOpen(), openStrategy, player);
    }

    public void close(WrappedInventory inventory, CorePlayer player){
        inventory.closeFor(player);
        if(inventory.viewers().isEmpty()) inventoryService.save(inventory);
    }

    public void fullClose(WrappedInventory inventory){
        Set<CorePlayer> viewers = inventory.viewers().keySet();
        viewers.forEach(player -> {
            if(player.getPlayer().getOpenInventory().getTopInventory() != null) player.getPlayer().closeInventory();
            close(inventory, player);
        });
    }

    public void openInventory(Inventory inventory, OpenStrategy openStrategy, CorePlayer player){
        switch(openStrategy){
            case TOTAL_BLOCKED: this.openTotalBlockedInventory(inventory, player); return;
            case FULL_EDIT: this.openInventory(inventory, player); return;
            default: this.openLowerDisabledInventory(inventory, player);
        }
    }

    public boolean isType(Inventory inventory, InventoryType type){
        return inventory != null && inventory.getType() == type;
    }

    public void openLowerDisabledInventory(Inventory inventory, CorePlayer player){
        server.schedule().runSync(() -> {
            player.getPlayer().playSound(player.getLocation(), Sound.CHEST_OPEN, 5, 5);
            if(!player.disableLowerInventory()) player.disableLowerInventory(true);
            player.getPlayer().openInventory(inventory);
        });
    }

    public void openTotalBlockedInventory(Inventory inventory, CorePlayer player){
        server.schedule().runSync(() -> {
            player.getPlayer().playSound(player.getLocation(), Sound.CHEST_OPEN, 5, 5);
            if(player.getPlayer().getOpenInventory() != null) player.getPlayer().closeInventory();
            if(!player.disableUpperInventory()) player.disableUpperInventory(true);
            if(!player.disableLowerInventory()) player.disableLowerInventory(true);
            player.getPlayer().openInventory(inventory);
        });
    }

    public void openInventory(Inventory inventory, CorePlayer player){
        player.getPlayer().playSound(player.getLocation(), Sound.CHEST_OPEN, 5, 5);
        server.schedule().runSync(() -> player.getPlayer().openInventory(inventory));
    }

    public WrappedInventory findOrCreate(String key, String title, byte slots){
        try{
            if(wrappedInventoryMap.containsKey(key)) return wrappedInventoryMap.get(key);
            WrappedInventory inventory = inventoryService.deserialize(false, key, title, slots);
            wrappedInventoryMap.put(key, inventory);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public WrappedInventory findOrCreateGlobal(String key, String title, byte slots){
        try{
            if(wrappedInventoryMap.containsKey(key)) return wrappedInventoryMap.get(key);
            WrappedInventory inventory = inventoryService.deserialize(true, key, title, slots);
            wrappedInventoryMap.put(key, inventory);
            return inventory;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void remove(String key){
        if(wrappedInventoryMap.containsKey(key))
            fullClose(wrappedInventoryMap.get(key));
        wrappedInventoryMap.remove(key);
    }

    @Override
    public String messageKey() {
        return "inventory";
    }

    @Override
    public void onReceive(final String data) {
        boolean contains = wrappedInventoryMap.containsKey(data);
        if(contains){
            try{
                WrappedInventory inv = wrappedInventoryMap.get(data);
                ItemCollection collection = inventoryService.findCollectionFor(inv.isGlobal(), inv.getKey());
                inv.setContents(collection.getItems());
            }catch (Exception ignore){}
        }
    }
}
