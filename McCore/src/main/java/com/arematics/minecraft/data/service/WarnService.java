package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.global.model.Warn;
import com.arematics.minecraft.data.global.repository.WarnsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = "warnCache", cacheManager = "globalCache")
public class WarnService {

    private final WarnsRepository repository;

    @Autowired
    public WarnService(WarnsRepository warnsRepository){
        this.repository = warnsRepository;
    }

    public List<Warn> getWarn(UUID uuid){
        return repository.findAllByUuidOrderByWarnedTimeDesc(uuid);
    }

    @CachePut(key = "#result.warnedTime")
    public Warn save(Warn warn){
        return repository.save(warn);
    }

    @CacheEvict(key = "#warn.warnedTime")
    public void remove(Warn warn){
        repository.delete(warn);
    }
}
