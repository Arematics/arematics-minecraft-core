package com.arematics.minecraft.guns.calculation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BodyLocation {
    HEAD(1.5),
    BODY(1.2),
    LEGS(1.0);

    private final double damageMultiplier;
}
