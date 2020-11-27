package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.repository.ModeOnlineTimeRepository;
import com.arematics.minecraft.data.share.model.OnlineTime;
import com.arematics.minecraft.data.share.repository.OnlineTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OnlineTimeService {

    private final OnlineTimeRepository repository;
    private final ModeOnlineTimeRepository modeOnlineTimeRepository;

    @Autowired
    public OnlineTimeService(OnlineTimeRepository onlineTimeRepository, ModeOnlineTimeRepository modeOnlineTimeRepository){
        this.repository = onlineTimeRepository;
        this.modeOnlineTimeRepository = modeOnlineTimeRepository;
    }

    @Cacheable(cacheNames = "onlineTime")
    public OnlineTime findByUUID(boolean mode, UUID uuid){
        Optional<OnlineTime> onlineTime;
        if(mode) onlineTime = modeOnlineTimeRepository.findById(uuid);
        else onlineTime = repository.findById(uuid);
        if(!onlineTime.isPresent()) throw new RuntimeException("OnlineTime for uuid: " + uuid.toString() + " could not be found");
        return onlineTime.get();
    }

    @CachePut(cacheNames = "onlineTime")
    public OnlineTime put(boolean mode, OnlineTime onlineTime){
        if(mode) return modeOnlineTimeRepository.save(onlineTime);
        else return repository.save(onlineTime);
    }
}
