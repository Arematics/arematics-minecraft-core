package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.ItemPrice;
import com.arematics.minecraft.data.mode.repository.ItemPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@CacheConfig(cacheNames = "itemprice")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemPriceService {
    private final ItemPriceRepository itempricerepository;

    @Cacheable(key = "#id")
    public ItemPrice findItemPrice(String id) {
        Optional<ItemPrice> result = itempricerepository.findById(id);
        if (!result.isPresent())
            throw new RuntimeException("Change this");
        return result.get();
    }

    @CachePut(key = "#result.id")
    public ItemPrice save(ItemPrice itemprice) {
        return itempricerepository.save(itemprice);
    }

    @CacheEvict(key = "#itemprice.id")
    public void remove(ItemPrice itemprice) {
        itempricerepository.delete(itemprice);
    }
}
