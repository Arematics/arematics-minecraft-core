package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.global.model.Warn;
import com.arematics.minecraft.data.global.repository.WarnsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WarnService {

    private final WarnsRepository repository;

    @Autowired
    public WarnService(WarnsRepository warnsRepository){
        this.repository = warnsRepository;
    }

    @Cacheable(cacheNames = "warnCache")
    public List<Warn> getWarn(UUID uuid){
        return repository.findAllByUuidOrderByWarnedTimeDesc(uuid);
    }

    @CachePut(cacheNames = "warnCache")
    public Warn save(Warn warn){
        return repository.save(warn);
    }

    @CacheEvict(cacheNames = "warnCache")
    public void remove(Warn warn){
        repository.delete(warn);
    }
}
