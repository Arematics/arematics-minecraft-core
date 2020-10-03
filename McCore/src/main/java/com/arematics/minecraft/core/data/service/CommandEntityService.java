package com.arematics.minecraft.core.data.service;

import com.arematics.minecraft.core.data.model.CommandEntity;
import com.arematics.minecraft.core.data.repository.CommandEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CommandEntityService {

    private final CommandEntityRepository repository;

    @Autowired
    public CommandEntityService(CommandEntityRepository commandEntityRepository){
        this.repository = commandEntityRepository;
    }

    public List<CommandEntity> fetchAll(){
        return repository.findAll();
    }

    @Cacheable(cacheNames = "commandEntities")
    public CommandEntity fetch(UUID uuid){
        Optional<CommandEntity> commandEntity = repository.findById(uuid);
        if(!commandEntity.isPresent()) throw new RuntimeException("CommandEntity with uuid: " + uuid.toString() +
                " could not be found");
        return commandEntity.get();
    }

    @Cacheable(cacheNames = "commandEntities")
    public void add(CommandEntity commandEntity){
        repository.save(commandEntity);
    }
}
