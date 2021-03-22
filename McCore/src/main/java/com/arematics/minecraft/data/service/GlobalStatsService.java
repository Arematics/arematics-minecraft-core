package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.GlobalStatisticData;
import com.arematics.minecraft.data.mode.repository.GlobalStatisticDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_=@Autowired)
@CacheConfig(cacheNames = "globalStats")
public class GlobalStatsService {
    private final GlobalStatisticDataRepository globalStatisticDataRepository;

    @Cacheable(key = "#id")
    public GlobalStatisticData findById(String id){
        Optional<GlobalStatisticData> data = globalStatisticDataRepository.findById(id);
        if(!data.isPresent())
            throw new RuntimeException("Could not find global data with id: " + id);
        return data.get();
    }

    @CachePut(key = "#result.type")
    public GlobalStatisticData update(GlobalStatisticData data){
        return globalStatisticDataRepository.save(data);
    }

    @CacheEvict(key = "#data.type")
    public void delete(GlobalStatisticData data){
        globalStatisticDataRepository.delete(data);
    }
}
