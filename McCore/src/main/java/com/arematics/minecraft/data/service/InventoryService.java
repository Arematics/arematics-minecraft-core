package com.arematics.minecraft.data.service;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.inventories.WrappedInventory;
import com.arematics.minecraft.data.redis.GlobalRedisMessagePublisher;
import com.arematics.minecraft.data.redis.MessagePublisher;
import com.arematics.minecraft.data.redis.ModeRedisMessagePublisher;
import com.arematics.minecraft.data.share.model.ItemCollection;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@CacheConfig(cacheNames = "inventories")
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class InventoryService implements GlobalMessageReceiveService, ModeMessageReceiveService {

    private final ModeRedisMessagePublisher modeRedisMessagePublisher;
    private final GlobalRedisMessagePublisher globalRedisMessagePublisher;
    private final GenericItemCollectionService genericItemCollectionService;
    private final Map<String, WrappedInventory> wrappedInventoryMap;

    public WrappedInventory findOrCreate(String key, String title, byte slots){
        try{
            if(wrappedInventoryMap.containsKey(key)) return wrappedInventoryMap.get(key);
            return deserialize(false, key, title, slots);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public WrappedInventory findOrCreateGlobal(String key, String title, byte slots){
        try{
            if(wrappedInventoryMap.containsKey(key)) return wrappedInventoryMap.get(key);
            return deserialize(true, key, title, slots);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void remove(String key){
        if(wrappedInventoryMap.containsKey(key))
            wrappedInventoryMap.get(key).closeForAll();
        wrappedInventoryMap.remove(key);
    }

    public void save(WrappedInventory wrappedInventory){
        genericItemCollectionService.save(wrappedInventory.isGlobal(), serialize(wrappedInventory));
        MessagePublisher publisher = wrappedInventory.isGlobal() ? globalRedisMessagePublisher : modeRedisMessagePublisher;
        publisher.publish(messageKey(), wrappedInventory.getKey());
    }

    private ItemCollection serialize(WrappedInventory wrappedInventory){
        return new ItemCollection(wrappedInventory.getKey(),
                CoreItem.create(wrappedInventory.getOpen().getContents()));
    }

    private ItemCollection findCollectionFor(boolean global, String key){
        ItemCollection collection;
        try{
            collection = genericItemCollectionService.findItemCollection(key);
        }catch (Exception exception){
            ItemCollection newCollection = new ItemCollection(key, new CoreItem[]{});
            collection = genericItemCollectionService.save(global, newCollection);
        }
        return collection;
    }

    private WrappedInventory deserialize(boolean global, String key, String title, byte slots){
        ItemCollection collection = findCollectionFor(global, key);
        Inventory inv = Bukkit.createInventory(null, slots, title);
        inv.setContents(collection.getItems());
        WrappedInventory wrappedInventory = new WrappedInventory(this, inv, key, global);
        wrappedInventoryMap.put(key, wrappedInventory);
        return wrappedInventory;
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
                ItemCollection collection = findCollectionFor(inv.isGlobal(), inv.getKey());
                inv.setContents(collection.getItems());
            }catch (Exception ignore){}
        }
    }
}
