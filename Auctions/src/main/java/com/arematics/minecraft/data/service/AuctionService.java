package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.global.model.EndTimeFilter;
import com.arematics.minecraft.data.mode.model.Auction;
import com.arematics.minecraft.data.mode.model.AuctionType;
import com.arematics.minecraft.data.mode.model.OwnAuctionFilter;
import com.arematics.minecraft.data.mode.model.PlayerAuctionSettings;
import com.arematics.minecraft.data.mode.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
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
import java.util.function.Supplier;

@Service
@CacheConfig(cacheNames = "auctions")
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class AuctionService {
    private final AuctionRepository auctionRepository;

    public Page<Auction> findAllOwnByFilter(UUID creator,
                                            Supplier<EndTimeFilter> sortSupplier,
                                            Supplier<OwnAuctionFilter> typeSupplier,
                                            int page){
        Sort sort = sortSupplier.get().getSort();
        Pageable pageable = PageRequest.of(page, 14, sort);
        OwnAuctionFilter filter = typeSupplier.get();
        switch (filter) {
            case NOT_ENDED:
                return auctionRepository.findAllByCreatorAndEndTimeIsAfter(creator,
                        Timestamp.valueOf(LocalDateTime.now()),
                        pageable);
            case ENDED:
                return auctionRepository.findAllByCreatorAndEndTimeIsBefore(creator,
                        Timestamp.valueOf(LocalDateTime.now()),
                        pageable);
            default:
                return auctionRepository.findAllByCreatorAndEndTimeIsAfter(creator,
                        Timestamp.valueOf(LocalDateTime.of(2000, 1, 1, 1, 1, 1)),
                        pageable);
        }
    }

    public Page<Auction> findAllByFilter(Supplier<PlayerAuctionSettings> supplier, int page){
        PlayerAuctionSettings settings = supplier.get();
        Sort sort = settings.getAuctionSort().getSort();
        Pageable pageable = PageRequest.of(page, 28, sort);
        Set<AuctionType> types = new HashSet<>();
        types.add(settings.getAuctionType());
        if(settings.getAuctionType().equals(AuctionType.ALL)){
            types.add(AuctionType.BID);
            types.add(AuctionType.INSTANT_BUY);
        }
        types.add(AuctionType.ALL);
        return auctionRepository.findAllByCreatorNotAndItemCategoryAndEndTimeIsAfterAndAuctionTypeIsInAndSoldIsFalse(
                settings.getUuid(),
                settings.getItemCategory(),
                Timestamp.valueOf(LocalDateTime.now()),
                types,
                pageable);
    }

    @Cacheable(key = "#auctionId")
    public Auction findById(Long auctionId){
        Optional<Auction> result = auctionRepository.findById(auctionId);
        if(!result.isPresent())
            throw new RuntimeException("Auction with auctionId: " + auctionId + " could not be found");
        return result.get();
    }

    @CachePut(key = "#result.auctionId")
    public Auction save(Auction auction){
        return auctionRepository.save(auction);
    }

    @CachePut(key = "#result.auctionId")
    public Auction create(Auction auction){
        auction.setAuctionId(null);
        return auctionRepository.save(auction);
    }

    @CacheEvict(key = "#auction.auctionId")
    public void delete(Auction auction){
        auctionRepository.delete(auction);
    }
}
