package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.global.model.ServerProperties;
import com.arematics.minecraft.data.global.repository.ServerPropertiesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@CacheConfig(cacheNames = "properties", cacheManager = "globalCache")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ServerPropertiesService {
    private final ServerPropertiesRepository serverpropertiesrepository;

    @Cacheable(key = "#id")
    public ServerProperties findServerProperties(String id) {
        Optional<ServerProperties> result = serverpropertiesrepository.findById(id);
        if (!result.isPresent())
            throw new RuntimeException("Change this");
        return result.get();
    }

    @CachePut(key = "#result.id")
    public ServerProperties save(ServerProperties serverproperties) {
        return serverpropertiesrepository.save(serverproperties);
    }

    @CacheEvict(key = "#serverproperties.id")
    public void remove(ServerProperties serverproperties) {
        serverpropertiesrepository.delete(serverproperties);
    }
}
