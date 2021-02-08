package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.Auction;
import com.arematics.minecraft.data.mode.model.AuctionCategory;
import com.arematics.minecraft.data.mode.model.AuctionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface AuctionRepository extends PagingAndSortingRepository<Auction, Long> {
    List<Auction> findAllByCreator(UUID creator);
    Page<Auction> findAllByAuctionCategoryAndAuctionTypeIsIn(AuctionCategory category, Set<AuctionType> type, Pageable pageable);
}
