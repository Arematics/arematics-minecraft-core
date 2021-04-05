package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.Bid;
import com.arematics.minecraft.data.mode.model.BidId;
import com.arematics.minecraft.data.mode.repository.BidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = "auctionBidCache")
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class BidService {

    private final BidRepository bidRepository;

    public Page<Bid> findAllByBidder(UUID bidder, int page){
        return bidRepository.findAllByBidder(bidder, PageRequest.of(page, 14));
    }

    @CachePut(key = "#result.auctionId.toString().concat(#result.bidder)")
    public Bid findById(BidId id){
        Optional<Bid> result = bidRepository.findById(id);
        if(!result.isPresent())
            throw new RuntimeException("Could not find bid with id: " + id.getAuctionId() + " and uuid: " + id.getBidder());
        return result.get();
    }

    @CacheEvict(cacheNames = "auctions", key = "#result.auctionId")
    @CachePut(key = "#result.auctionId.toString().concat(#result.bidder)")
    public Bid save(Bid bid){
        return bidRepository.save(bid);
    }

    @CacheEvict(key = "#bid.auctionId.toString().concat(#bid.bidder)")
    public void remove(Bid bid){
        bidRepository.delete(bid);
    }
}
