package com.arematics.minecraft.core.server.entities.player.inventories.anvil;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum AnvilSlot {
    LEFT(0),
    RIGHT(1),
    RESULT(2);

    private final int slot;

    public static AnvilSlot bySlot(int slot){
        return Arrays.stream(AnvilSlot.values()).filter(eslot -> eslot.slot == slot).findAny().orElse(AnvilSlot.RESULT);
    }
}
