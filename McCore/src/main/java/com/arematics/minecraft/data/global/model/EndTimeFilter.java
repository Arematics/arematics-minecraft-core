package com.arematics.minecraft.data.global.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum EndTimeFilter {
    ENDING_SOON(Sort.by("endTime").ascending()),
    ENDING_LATE(Sort.by("endTime").descending());

    private final Sort sort;
}
