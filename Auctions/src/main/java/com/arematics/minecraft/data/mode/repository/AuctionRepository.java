package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.Auction;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface AuctionRepository extends PagingAndSortingRepository<Auction, Long> {
    List<Auction> findAllByCreator(UUID creator);
}
