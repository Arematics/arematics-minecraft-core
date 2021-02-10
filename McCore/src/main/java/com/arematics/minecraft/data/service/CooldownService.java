package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.repository.ModeCooldownRepository;
import com.arematics.minecraft.data.share.model.Cooldown;
import com.arematics.minecraft.data.share.model.CooldownKey;
import com.arematics.minecraft.data.share.repository.CooldownRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = "cooldowns")
public class CooldownService {

    private final List<CooldownRepository> repositories;

    @Autowired
    public CooldownService(CooldownRepository cooldownRepository, ModeCooldownRepository modeCooldownRepository){
        this.repositories = new ArrayList<>();
        this.repositories.add(modeCooldownRepository);
        this.repositories.add(cooldownRepository);
    }

    @Cacheable(key = "#key")
    public Optional<Long> getCooldown(CooldownKey key){
        return repositories.stream()
                .map(repository -> repository.findById(key))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Cooldown::getEndTime)
                .findFirst();
    }

    public boolean hasCooldown(CooldownKey key){
        Optional<Long> cooldown = getCooldown(key);
        return cooldown.filter(aLong -> System.currentTimeMillis() < aLong).isPresent();
    }

    /**
     * Creates new cooldown with key and secondKey and time in seconds
     * @param mode Mode DB or global db saving
     * @param key Key Value
     * @param time Time in seconds
     */
    @CachePut(key = "#result.cooldownKey")
    public Cooldown of(boolean mode, CooldownKey key, long time){
        Cooldown cooldown = new Cooldown(key, System.currentTimeMillis() + (time * 1000));
        return mode ? repositories.get(0).save(cooldown) : repositories.get(1).save(cooldown);
    }
}
