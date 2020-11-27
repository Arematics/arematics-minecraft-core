package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.CrystalKey;
import com.arematics.minecraft.data.mode.repository.CrystalKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CrystalKeyService {

    private final CrystalKeyRepository repository;

    @Autowired
    public CrystalKeyService(CrystalKeyRepository crystalKeyRepository){
        this.repository = crystalKeyRepository;
    }

    @Cacheable(cacheNames = "crystalCache")
    public CrystalKey findById(String name){
        Optional<CrystalKey> crystalKey = repository.findById(name);
        if(!crystalKey.isPresent()) throw new RuntimeException("Crystal with name: " + name + " could not be found");
        return crystalKey.get();
    }

    @CachePut(cacheNames = "crystalCache")
    public CrystalKey update(CrystalKey crystalKey){
        return this.repository.save(crystalKey);
    }

    @CacheEvict(cacheNames = "crystalCache")
    public void delete(CrystalKey crystalKey){
        this.repository.delete(crystalKey);
    }
}
