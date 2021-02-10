package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.ServerBuff;
import com.arematics.minecraft.data.mode.repository.ServerBuffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@CacheConfig(cacheNames = "server_buffs")
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class ServerBuffService {
    private final ServerBuffRepository serverBuffRepository;

    @Cacheable(key = "#id")
    public ServerBuff findServerBuff(String id){
        Optional<ServerBuff> result = serverBuffRepository.findById(id);
        if(!result.isPresent())
            throw new RuntimeException("Could not find server buff with id: " + id);
        return result.get();
    }

    @CachePut(key = "#result.id")
    public ServerBuff update(ServerBuff buff){
        return serverBuffRepository.save(buff);
    }

    @CacheEvict(key = "#buff.id")
    public void delete(ServerBuff buff){
        serverBuffRepository.delete(buff);
    }
}
