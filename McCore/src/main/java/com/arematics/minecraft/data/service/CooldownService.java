package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.repository.ModeCooldownRepository;
import com.arematics.minecraft.data.share.model.Cooldown;
import com.arematics.minecraft.data.share.model.CooldownKey;
import com.arematics.minecraft.data.share.repository.CooldownRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@CacheConfig(cacheNames = "cooldowns")
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class CooldownService {

    private final CooldownRepository cooldownRepository;
    private final ModeCooldownRepository modeCooldownRepository;

    @Cacheable(key = "#key.id + #key.secondKey")
    public Optional<Cooldown> getModeCooldown(CooldownKey key){
        return modeCooldownRepository.findById(key);
    }

    @Cacheable(key = "#key.id + #key.secondKey", cacheManager = "globalCache")
    public Optional<Cooldown> getGlobalCooldown(CooldownKey key){
        return cooldownRepository.findById(key);
    }

    public boolean hasCooldown(CooldownKey key){
        Optional<Cooldown> cooldown = getModeCooldown(key);
        Optional<Cooldown> cooldownGlobal = getGlobalCooldown(key);
        return cooldown.filter(aLong -> System.currentTimeMillis() < aLong.getEndTime()).isPresent()
                || cooldownGlobal.filter(aLong -> System.currentTimeMillis() < aLong.getEndTime()).isPresent();
    }

    /**
     * Creates new cooldown with key and secondKey and time in seconds
     */
    @CachePut(key = "#result.id + #result.secondKey")
    public Cooldown ofMode(Cooldown cooldown){
        return modeCooldownRepository.save(cooldown);
    }
    @CachePut(key = "#result.id + #result.secondKey")
    public Cooldown ofGlobal(Cooldown cooldown){
        return cooldownRepository.save(cooldown);
    }
}
