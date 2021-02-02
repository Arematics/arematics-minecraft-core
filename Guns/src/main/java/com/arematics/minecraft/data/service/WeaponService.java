package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.Weapon;
import com.arematics.minecraft.data.mode.model.WeaponType;
import com.arematics.minecraft.data.mode.repository.WeaponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WeaponService {
    private final WeaponRepository weaponRepository;
    private final Map<String, Weapon> weaponCache = new HashMap<>();
    private final Map<WeaponType, List<String>> weaponTypeIdCache = new HashMap<>();

    @Autowired
    public WeaponService(WeaponRepository weaponRepository){
        this.weaponRepository = weaponRepository;
        init();
    }

    public void init(){
        fetchAll().forEach(weapon -> {
            weaponCache.put(weapon.getId(), weapon);
            List<String> ids = weaponTypeIdCache.getOrDefault(weapon.getType(), new ArrayList<>());
            ids.add(weapon.getId());
            weaponTypeIdCache.put(weapon.getType(), ids);
        });
    }

    public Weapon fetchWeapon(String id){
        if(weaponCache.containsKey(id)) return weaponCache.get(id);
        return findById(id);
    }

    public List<Weapon> findAllByType(WeaponType weaponType){
        return findAllById(weaponTypeIdCache.get(weaponType));
    }

    public List<Weapon> findAllById(List<String> ids){
        return weaponCache.values().stream().filter(weapon -> ids.contains(weapon.getId())).collect(Collectors.toList());
    }

    public List<Weapon> fetchAll(){
        return weaponRepository.findAll();
    }

    public Weapon findById(String id){
        Optional<Weapon> weapon = this.weaponRepository.findById(id);
        if(!weapon.isPresent())
            throw new RuntimeException("Could not find weapon with id: " + id);
        Weapon result = weapon.get();
        weaponCache.put(result.getId(), result);
        return result;
    }

    public Weapon update(Weapon weapon){
        weaponCache.put(weapon.getId(), weapon);
        return this.weaponRepository.save(weapon);
    }

    public void delete(Weapon weapon){
        weaponCache.remove(weapon.getId());
        this.weaponRepository.delete(weapon);
    }
}
