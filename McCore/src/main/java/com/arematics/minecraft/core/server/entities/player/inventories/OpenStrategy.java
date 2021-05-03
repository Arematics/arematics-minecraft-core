package com.arematics.minecraft.core.server.entities.player.inventories;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OpenStrategy {
    DEFAULT(false, true),
    TOTAL_BLOCKED(false, false),
    FULL_EDIT(true, true);

    private final boolean editLower;
    private final boolean editUpper;
}
