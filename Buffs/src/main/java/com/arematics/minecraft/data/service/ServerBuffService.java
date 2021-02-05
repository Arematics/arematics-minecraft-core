package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.ServerBuff;
import com.arematics.minecraft.data.mode.repository.ServerBuffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class ServerBuffService {
    private final ServerBuffRepository serverBuffRepository;

    @Cacheable(cacheNames = "server_buffs", key = "#id")
    public ServerBuff findServerBuff(String id){
        Optional<ServerBuff> result = serverBuffRepository.findById(id);
        if(!result.isPresent())
            throw new RuntimeException("Could not find server buff with id: " + id);
        return result.get();
    }

    @CachePut(cacheNames = "server_buffs", key = "#result.id")
    public ServerBuff update(ServerBuff buff){
        return serverBuffRepository.save(buff);
    }

    @CacheEvict(cacheNames = "server_buffs", key = "#buff.id")
    public void delete(ServerBuff buff){
        serverBuffRepository.delete(buff);
    }
}
