package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.Bid;
import com.arematics.minecraft.data.mode.model.BidId;
import com.arematics.minecraft.data.mode.repository.BidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@CacheConfig(cacheNames = "auctionBidCache")
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class BidService {

    private final BidRepository bidRepository;

    @CachePut(key = "#result.auctionId.toString().concat(#result.bidder)")
    public Bid findById(BidId id){
        Optional<Bid> result = bidRepository.findById(id);
        if(!result.isPresent())
            throw new RuntimeException("Could not find bid with id: " + id.getAuctionId() + " and uuid: " + id.getBidder());
        return result.get();
    }

    @CachePut(key = "#result.auctionId.toString().concat(#result.bidder)")
    public Bid save(Bid bid){
        return bidRepository.save(bid);
    }
}
