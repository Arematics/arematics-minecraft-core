package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.GameMap;
import com.arematics.minecraft.data.mode.repository.MapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MapService {

    private final MapRepository repository;

    @Autowired
    public MapService(MapRepository mapRepository){
        this.repository = mapRepository;
    }

    public GameMap findById(String id){
        Optional<GameMap> map = repository.findById(id);
        if(!map.isPresent())
            throw new RuntimeException("Map with id: " + id + " could not be found");
        return map.get();
    }

    public List<String> findAllIds(){
        return repository.findAllIds();
    }

    public GameMap save(GameMap map){
        return repository.save(map);
    }

    public void remove(GameMap map){
        repository.delete(map);
    }
}
