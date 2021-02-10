package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.AuctionCategory;
import com.arematics.minecraft.data.mode.repository.AuctionCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class AuctionCategoryService {
    private final AuctionCategoryRepository auctionCategoryRepository;

    @Cacheable(cacheNames = "auction_category", key = "#id")
    public AuctionCategory findById(String id){
        Optional<AuctionCategory> result = auctionCategoryRepository.findById(id);
        if(!result.isPresent())
            throw new RuntimeException("AuctionCategory with id: " + id + " could not be found");
        return result.get();
    }

    @CachePut(cacheNames = "auction_category", key = "#result.categoryId")
    public AuctionCategory save(AuctionCategory auctionCategory){
        return auctionCategoryRepository.save(auctionCategory);
    }

    @CacheEvict(cacheNames = "auction_category", key = "#auctionCategory.categoryId")
    public void delete(AuctionCategory auctionCategory){
        auctionCategoryRepository.delete(auctionCategory);
    }
}
