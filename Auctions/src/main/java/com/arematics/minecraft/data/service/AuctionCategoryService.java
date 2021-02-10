package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.AuctionCategory;
import com.arematics.minecraft.data.mode.repository.AuctionCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@CacheConfig(cacheNames = "auction_category")
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class AuctionCategoryService {
    private final AuctionCategoryRepository auctionCategoryRepository;

    @Cacheable(key = "#id")
    public AuctionCategory findById(String id){
        Optional<AuctionCategory> result = auctionCategoryRepository.findById(id);
        if(!result.isPresent())
            throw new RuntimeException("AuctionCategory with id: " + id + " could not be found");
        return result.get();
    }

    @CachePut(key = "#result.categoryId")
    public AuctionCategory save(AuctionCategory auctionCategory){
        return auctionCategoryRepository.save(auctionCategory);
    }

    @CacheEvict(key = "#auctionCategory.categoryId")
    public void delete(AuctionCategory auctionCategory){
        auctionCategoryRepository.delete(auctionCategory);
    }
}
