package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.WeaponType;
import com.arematics.minecraft.data.mode.model.WeaponTypeData;
import com.arematics.minecraft.data.mode.repository.WeaponTypeDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@CacheConfig(cacheNames = "weapon_type_data")
@RequiredArgsConstructor(onConstructor_= @Autowired)
public class WeaponTypeDataService {
    private final WeaponTypeDataRepository weaponTypeDataRepository;

    @Cacheable(key = "#weaponType")
    public WeaponTypeData findById(WeaponType weaponType){
        Optional<WeaponTypeData> data = weaponTypeDataRepository.findById(weaponType);
        if(!data.isPresent())
            throw new RuntimeException("Could not find data weapon type: " + weaponType.name());
        return data.get();
    }

    @CachePut(key = "#result.weaponType")
    public WeaponTypeData update(WeaponTypeData weaponTypeData){
        return this.weaponTypeDataRepository.save(weaponTypeData);
    }

    @CacheEvict(key = "#weaponTypeData.weaponType")
    public void delete(WeaponTypeData weaponTypeData){
        this.weaponTypeDataRepository.delete(weaponTypeData);
    }
}
