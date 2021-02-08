package com.arematics.minecraft.data.service;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.data.mode.model.*;
import com.arematics.minecraft.data.mode.repository.AuctionRepository;
import com.sk89q.worldguard.blacklist.action.ActionType;
import org.bukkit.Material;
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
public class AuctionService {
    private final AuctionRepository auctionRepository;

    @Autowired
    public AuctionService(AuctionRepository auctionRepository, AuctionCategoryService auctionCategoryService, BidService bidService){
        this.auctionRepository = auctionRepository;
        AuctionCategory category = auctionCategoryService.save(new AuctionCategory("stone", new CoreItem[]{CoreItem.generate(Material.STONE)}, new CoreItem[]{CoreItem.generate(Material.STONE)}));
        Auction auction = new Auction(0L, UUID.fromString("84e0aac6-aede-41a1-a912-e5f762e33684"), 500.0, 0.0,
                new CoreItem[]{CoreItem.generate(Material.STONE)},
                category,
                AuctionType.ALL,
                Timestamp.valueOf(LocalDateTime.now().plusHours(5)),
                new HashSet<>());
        auction = create(auction);
        auction.getBids().add(bidService.save(new Bid(auction.getAuctionId(), UUID.fromString("84e0aac6-aede-41a1-a912-e5f762e33684"), 3466437)));
        save(auction);
        Auction auction2 = new Auction(0L, UUID.fromString("84e0aac6-aede-41a1-a912-e5f762e33684"), 0, 100.0,
                new CoreItem[]{CoreItem.generate(Material.STONE)},
                category,
                AuctionType.INSTANT_BUY,
                Timestamp.valueOf(LocalDateTime.now().plusHours(6)),
                new HashSet<>());
        auction2 = create(auction2);
        auction2.getBids().add(bidService.save(new Bid(auction2.getAuctionId(), UUID.fromString("84e0aac6-aede-41a1-a912-e5f762e33684"), 23523)));
        save(auction2);
    }

    public List<Auction> findAllByCreator(UUID creator){
        return auctionRepository.findAllByCreator(creator);
    }

    public Page<Auction> findAllByFilter(PlayerAuctionSettings settings, int page){
        Sort sort;
        switch (settings.getAuctionSort()){
            case LOWEST_BID: sort = Sort.by("bids.amount").descending(); break;
            case MOST_BIDS: sort = Sort.by("bids").ascending(); break;
            case ENDING_SOON: sort = Sort.by("endTime").ascending(); break;
            case HIGHEST_BID:
            default: sort = Sort.by("bids.amount").ascending();
        }
        Pageable pageable = PageRequest.of(page, 28, sort);
        Set<AuctionType> types = new HashSet<>();
        types.add(settings.getAuctionType());
        if(settings.getAuctionType().equals(AuctionType.ALL)){
            types.add(AuctionType.BID);
            types.add(AuctionType.INSTANT_BUY);
        }
        types.add(AuctionType.ALL);
        return auctionRepository.findAllByAuctionCategoryAndAuctionTypeIsIn(settings.getCategory(), types, pageable);
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
