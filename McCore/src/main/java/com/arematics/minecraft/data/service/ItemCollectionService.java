package com.arematics.minecraft.data.service;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.data.mode.repository.ModeItemCollectionRepository;
import com.arematics.minecraft.data.share.model.ItemCollection;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = "itemcollection")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemCollectionService implements ModeMessageReceiveService{

    private final ModeItemCollectionRepository modeItemCollectionRepository;

    public ItemCollection findOrCreate(String id){
        try{
            return findItemCollection(id);
        }catch (RuntimeException re){
            return save(new ItemCollection(id, new CoreItem[]{}));
        }
    }

    @Cacheable(key = "#id")
    public ItemCollection findItemCollection(String id) {
        Optional<ItemCollection> result = modeItemCollectionRepository.findById(id);
        if (!result.isPresent())
            throw new RuntimeException("Change this");
        return result.get();
    }

    @CachePut(key = "#result.id")
    public ItemCollection save(ItemCollection itemcollection) {
        return modeItemCollectionRepository.save(itemcollection);
    }

    @CacheEvict(key = "#itemcollection.id")
    public void remove(ItemCollection itemcollection) {
        modeItemCollectionRepository.delete(itemcollection);
    }

    @Override
    public String messageKey() {
        return "itemCollection";
    }

    @Override
    public void onReceive(String data) {
        try{
            UUID uuid = UUID.fromString(data);
            Player player = Bukkit.getPlayer(uuid);
            if(player != null){
                ItemCollection collection = findOrCreate(uuid + ".playerInv");
                player.getInventory().setContents(collection.getItems());
            }
        }catch (Exception ignore){}
    }
}
