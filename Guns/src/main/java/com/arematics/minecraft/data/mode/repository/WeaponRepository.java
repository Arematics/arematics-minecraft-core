package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.Weapon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeaponRepository extends JpaRepository<Weapon, String> {
}
