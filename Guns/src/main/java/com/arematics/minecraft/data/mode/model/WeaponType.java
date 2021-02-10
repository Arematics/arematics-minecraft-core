package com.arematics.minecraft.data.mode.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WeaponType {
    MACHINE_GUN(0.1f),
    SNIPER(0.05f),
    SHOTGUN(0.3f);

    private final float accuracy;
}
