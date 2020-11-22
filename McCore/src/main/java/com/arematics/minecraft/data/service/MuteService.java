package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.global.model.Mute;
import com.arematics.minecraft.data.global.repository.MutesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class MuteService {

    private final MutesRepository repository;

    @Autowired
    public MuteService(MutesRepository mutesRepository){
        this.repository = mutesRepository;
    }

    @Cacheable(cacheNames = "muteCache")
    public Mute getMute(UUID uuid){
        Optional<Mute> mute = repository.findById(uuid);
        if(!mute.isPresent())
            throw new RuntimeException("Mute for uuid: " + uuid.toString() + " could not be found");
        return mute.get();
    }

    @CachePut(cacheNames = "muteCache")
    public Mute save(Mute mute){
        return repository.save(mute);
    }

    @CacheEvict(cacheNames = "muteCache")
    public void remove(Mute mute){
        repository.delete(mute);
    }
}
