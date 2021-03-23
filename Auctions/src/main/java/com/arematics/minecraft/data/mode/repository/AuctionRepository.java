package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.core.server.items.ItemCategory;
import com.arematics.minecraft.data.mode.model.Auction;
import com.arematics.minecraft.data.mode.model.AuctionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface AuctionRepository extends PagingAndSortingRepository<Auction, Long> {
    List<Auction> findAllByCreator(UUID creator);
    Page<Auction> findAllByCreatorAndEndTimeIsAfter(UUID creator, Timestamp date, Pageable pageable);
    Page<Auction> findAllByCreatorAndEndTimeIsBefore(UUID creator, Timestamp date, Pageable pageable);
    Page<Auction> findAllByItemCategoryAndEndTimeIsAfterAndAuctionTypeIsIn(ItemCategory category,
                                                                           Timestamp timestamp,
                                                                           Set<AuctionType> type,
                                                                           Pageable pageable);
}
