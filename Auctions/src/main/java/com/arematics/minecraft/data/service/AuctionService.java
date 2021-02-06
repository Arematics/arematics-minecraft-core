package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.Auction;
import com.arematics.minecraft.data.mode.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class AuctionService {
    private final AuctionRepository auctionRepository;

    public List<Auction> findAllByCreator(UUID creator){
        return auctionRepository.findAllByCreator(creator);
    }

    @Cacheable(cacheNames = "auctions", key = "#auctionId")
    public Auction findById(Long auctionId){
        Optional<Auction> result = auctionRepository.findById(auctionId);
        if(!result.isPresent())
            throw new RuntimeException("Auction with auctionId: " + auctionId + " could not be found");
        return result.get();
    }

    @CachePut(cacheNames = "auctions", key = "#result.auctionId")
    public Auction save(Auction auction){
        return auctionRepository.save(auction);
    }

    @CacheEvict(cacheNames = "auctions", key = "#auction.auctionId")
    public void delete(Auction auction){
        auctionRepository.delete(auction);
    }
}
