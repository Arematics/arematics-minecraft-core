package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.Weapon;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeaponService {

    private final Map<String, Weapon> weaponCache = new HashMap<>();
}
