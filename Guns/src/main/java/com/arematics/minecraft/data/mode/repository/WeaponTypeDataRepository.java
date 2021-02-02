package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.WeaponType;
import com.arematics.minecraft.data.mode.model.WeaponTypeData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeaponTypeDataRepository extends JpaRepository<WeaponTypeData, WeaponType> {
}
