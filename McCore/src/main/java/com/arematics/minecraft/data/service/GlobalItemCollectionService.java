package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.share.model.ItemCollection;
import com.arematics.minecraft.data.share.repository.ItemCollectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@CacheConfig(cacheNames = "globalitemcollection", cacheManager = "globalCache")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GlobalItemCollectionService implements GlobalMessageReceiveService{
    private final ItemCollectionRepository itemCollectionRepository;

    @Cacheable(key = "#id")
    public ItemCollection findItemCollection(String id) {
        Optional<ItemCollection> result = itemCollectionRepository.findById(id);
        if (!result.isPresent())
            throw new RuntimeException("Change this");
        return result.get();
    }

    @CachePut(key = "#result.id")
    public ItemCollection save(ItemCollection itemCollection) {
        return itemCollectionRepository.save(itemCollection);
    }

    @CacheEvict(key = "#itemCollection.id")
    public void remove(ItemCollection itemCollection) {
        itemCollectionRepository.delete(itemCollection);
    }

    @Override
    public String messageKey() {
        return "itemCollection";
    }

    @Override
    public void onReceive(String data) {

    }
}
