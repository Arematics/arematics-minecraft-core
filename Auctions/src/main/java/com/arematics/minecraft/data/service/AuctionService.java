package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.Auction;
import com.arematics.minecraft.data.mode.model.AuctionType;
import com.arematics.minecraft.data.mode.model.PlayerAuctionSettings;
import com.arematics.minecraft.data.mode.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class AuctionService {
    private final AuctionRepository auctionRepository;

    public List<Auction> findAllByCreator(UUID creator){
        return auctionRepository.findAllByCreator(creator);
    }

    public Page<Auction> findAllByFilter(PlayerAuctionSettings settings, int page){
        Sort sort = settings.getAuctionSort().getSort();
        Pageable pageable = PageRequest.of(page, 28, sort);
        Set<AuctionType> types = new HashSet<>();
        types.add(settings.getAuctionType());
        if(settings.getAuctionType().equals(AuctionType.ALL)){
            types.add(AuctionType.BID);
            types.add(AuctionType.INSTANT_BUY);
        }
        types.add(AuctionType.ALL);
        return auctionRepository.findAllByAuctionCategoryAndEndTimeIsAfterAndAuctionTypeIsIn(settings.getCategory(),
                Timestamp.valueOf(LocalDateTime.now()),
                types,
                pageable);
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

    @CachePut(cacheNames = "auctions", key = "#result.auctionId")
    public Auction create(Auction auction){
        auction.setAuctionId(null);
        return auctionRepository.save(auction);
    }

    @CacheEvict(cacheNames = "auctions", key = "#auction.auctionId")
    public void delete(Auction auction){
        auctionRepository.delete(auction);
    }
}
