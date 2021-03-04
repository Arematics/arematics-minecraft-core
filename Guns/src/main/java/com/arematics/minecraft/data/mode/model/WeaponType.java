package com.arematics.minecraft.data.mode.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WeaponType {
    MACHINE_GUN(0.03f, 2.2f, 0.1f, 2),
    SNIPER(0.05f, 3f,1f, 3),
    PISTOL(0.01f, 2f, 0.3f, 1),
    SHOTGUN(0.1f,  1.8f, 0.5f, 2);

    private final float accuracy;
    private final float speed;
    private final float shootSpeed;
    private final int loadingSpeed;
}
