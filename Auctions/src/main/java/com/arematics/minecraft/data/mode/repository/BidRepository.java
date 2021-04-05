package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.Bid;
import com.arematics.minecraft.data.mode.model.BidId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface BidRepository extends PagingAndSortingRepository<Bid, BidId> {
    Page<Bid> findAllByBidder(UUID bidder, Pageable pageable);
}
