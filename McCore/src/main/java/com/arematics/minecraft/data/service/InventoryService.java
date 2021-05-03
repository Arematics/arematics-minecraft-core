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

@Service
@CacheConfig(cacheNames = "inventories")
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class InventoryService {

    private final ModeRedisMessagePublisher modeRedisMessagePublisher;
    private final GlobalRedisMessagePublisher globalRedisMessagePublisher;
    private final GenericItemCollectionService genericItemCollectionService;

    public void save(WrappedInventory wrappedInventory){
        genericItemCollectionService.save(wrappedInventory.isGlobal(), serialize(wrappedInventory));
        MessagePublisher publisher = wrappedInventory.isGlobal() ? globalRedisMessagePublisher : modeRedisMessagePublisher;
        publisher.publish("inventory", wrappedInventory.getKey());
    }

    public ItemCollection serialize(WrappedInventory wrappedInventory){
        return new ItemCollection(wrappedInventory.getKey(),
                CoreItem.create(wrappedInventory.getOpen().getContents()));
    }

    public WrappedInventory deserialize(boolean global, String key, String title, byte slots){
        ItemCollection collection = findCollectionFor(global, key);
        Inventory inv = Bukkit.createInventory(null, slots, title);
        inv.setContents(collection.getItems());
        return new WrappedInventory(inv, key, global);
    }

    public ItemCollection findCollectionFor(boolean global, String key){
        ItemCollection collection;
        try{
            collection = genericItemCollectionService.findItemCollection(key);
        }catch (Exception exception){
            ItemCollection newCollection = new ItemCollection(key, new CoreItem[]{});
            collection = genericItemCollectionService.save(global, newCollection);
        }
        return collection;
    }
}
