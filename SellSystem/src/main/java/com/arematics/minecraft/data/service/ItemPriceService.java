package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.ItemPrice;
import com.arematics.minecraft.data.mode.repository.ItemPriceRepository;
import com.arematics.minecraft.sells.commands.SellCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;

@Service
@CacheConfig(cacheNames = "itemprice")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemPriceService {
    private final ItemPriceRepository itempricerepository;

    public Page<ItemPrice> fetchPrices(int page, Supplier<SellCommand.SellListFilter> filter){
        Pageable pageable = PageRequest.of(page, 4 * 7, filter.get().getSort());
        return itempricerepository.findAll(pageable);
    }

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
