package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.global.model.SoulOg;
import com.arematics.minecraft.data.global.repository.SoulOgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = "ogCache", cacheManager = "globalCache")
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class OgService {
    private final SoulOgRepository soulOgRepository;

    @Cacheable(key = "#uuid")
    public SoulOg findByUUID(UUID uuid){
        Optional<SoulOg> og = soulOgRepository.findById(uuid);
        if(!og.isPresent())
            throw new RuntimeException("Could not find og with uuid: " + uuid);
        return og.get();
    }
}
