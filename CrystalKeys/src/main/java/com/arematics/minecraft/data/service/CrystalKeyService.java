package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.CrystalKey;
import com.arematics.minecraft.data.mode.repository.CrystalKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames={"crystalCache"})
public class CrystalKeyService {

    private final CrystalKeyRepository repository;

    @Autowired
    public CrystalKeyService(CrystalKeyRepository crystalKeyRepository){
        this.repository = crystalKeyRepository;
    }

    public List<String> findAllNames(){
        return repository.findAllNames();
    }

    @Cacheable(key = "#name")
    public CrystalKey findById(String name){
        Optional<CrystalKey> crystalKey = repository.findById(name);
        if(!crystalKey.isPresent()) throw new RuntimeException("Crystal with name: " + name + " could not be found");
        return crystalKey.get();
    }

    @CachePut(key = "#crystalKey.name")
    public CrystalKey update(CrystalKey crystalKey){
        return this.repository.save(crystalKey);
    }

    @CacheEvict(key = "#crystalKey.name")
    public void delete(CrystalKey crystalKey){
        this.repository.delete(crystalKey);
    }
}
