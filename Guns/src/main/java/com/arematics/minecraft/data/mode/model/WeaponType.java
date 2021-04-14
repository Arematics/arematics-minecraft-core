package com.arematics.minecraft.data.mode.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WeaponType {
    MACHINE_GUN(0.03f, 2.2f, 0.3f, 3),
    SNIPER(0.05f, 3f,1f, 4),
    PISTOL(0.01f, 2f, 0.4f, 1),
    SHOTGUN(0.1f,  1.8f, 0.6f, 2);

    //Accuracy of the weapon
    private final float accuracy;
    //Speed of the bullet
    private final float speed;
    //Speed of bullet launch
    private final float shootSpeed;
    //Speed of bullet reload
    private final int loadingSpeed;
}
