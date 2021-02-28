package com.arematics.minecraft.data.mode.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WeaponType {
    MACHINE_GUN(0.05f, 1.9f, 2),
    SNIPER(0.07f, 2.5f,3),
    PISTOL(0.01f, 1.7f, 1),
    SHOTGUN(0.3f,  1.4f, 2);

    private final float accuracy;
    private final float speed;
    private final int loadingSpeed;
}
