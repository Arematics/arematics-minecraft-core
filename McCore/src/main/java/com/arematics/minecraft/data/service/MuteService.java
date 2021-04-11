package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.global.model.Mute;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = "mute", cacheManager = "globalCache")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MuteService {

    private final Map<UUID, Mute> muted = new HashMap<>();

    @Cacheable(key = "#id")
    public Mute findMute(UUID id) {
        if(!this.muted.containsKey(id)) throw new RuntimeException("Not muted");
        return this.muted.get(id);
    }

    @CachePut(key = "#result.uuid")
    public Mute save(Mute mute) {
        this.muted.put(mute.getUuid(), mute);
        return mute;
    }

    @CacheEvict(key = "#mute.uuid")
    public void remove(Mute mute) {
        this.muted.remove(mute.getUuid());
    }

    public boolean isMuted(UUID uuid){
        try{
            Mute mute = findMute(uuid);
            if(mute.getMuteUntil().after(Timestamp.valueOf(LocalDateTime.now())))
                return true;
            else
                this.muted.remove(mute.getUuid());
        }catch (Exception ignore){}
        return false;
    }
}
