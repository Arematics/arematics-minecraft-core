package com.arematics.minecraft.data.mode.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum AuctionSort {
    HIGHEST_BID(Sort.by("bids.amount").descending()),
    LOWEST_BID(Sort.by("bids.amount").ascending()),
    ENDING_SOON(Sort.by("bids").descending()),
    MOST_BIDS(Sort.by("endTime").ascending());

    private final Sort sort;
}
