package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.CommandEntity;
import com.arematics.minecraft.data.mode.repository.CommandEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = "commandEntities")
public class CommandEntityService {

    private final CommandEntityRepository repository;

    @Autowired
    public CommandEntityService(CommandEntityRepository commandEntityRepository){
        this.repository = commandEntityRepository;
    }

    public List<CommandEntity> fetchAll(){
        return repository.findAll();
    }

    @Cacheable(key = "#uuid")
    public CommandEntity fetch(UUID uuid){
        Optional<CommandEntity> commandEntity = repository.findById(uuid);
        if(!commandEntity.isPresent()) throw new RuntimeException("CommandEntity with uuid: " + uuid.toString() +
                " could not be found");
        return commandEntity.get();
    }

    @CachePut(key = "#result.uuid")
    public CommandEntity add(CommandEntity commandEntity){
        return repository.save(commandEntity);
    }
}
