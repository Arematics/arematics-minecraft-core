package com.arematics.minecraft.data.mode.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum AuctionSort {
    HIGHEST_BID(Sort.by("bids.amount").descending()),
    LOWEST_BID(Sort.by("bids.amount").ascending()),
    ENDING_SOON(Sort.by("endTime").ascending()),
    MOST_BIDS(Sort.by("bids").descending());

    private final Sort sort;
}
