package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.global.model.Warn;
import com.arematics.minecraft.data.global.repository.WarnsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = "warnCache", cacheManager = "globalCache")
public class WarnService {

    private final WarnsRepository repository;

    @Autowired
    public WarnService(WarnsRepository warnsRepository){
        this.repository = warnsRepository;
    }

    public List<Warn> getWarns(UUID uuid){
        return repository.findAllByUuidOrderByWarnedTimeDesc(uuid);
    }

    public int getWarnAmount(UUID uuid){
        Integer amount = repository.countByAmountOfUuid(uuid);
        return amount == null ? 0 : amount;
    }

    @Cacheable(key = "#id")
    public Warn findById(Long id){
        Optional<Warn> result = repository.findById(id);
        if(!result.isPresent())
            throw new RuntimeException("Could not find warn with id: " + id);
        return result.get();
    }

    @CachePut(key = "#result.id")
    public Warn save(Warn warn){
        return repository.save(warn);
    }

    @CacheEvict(key = "#warn.id")
    public void remove(Warn warn){
        repository.delete(warn);
    }
}
