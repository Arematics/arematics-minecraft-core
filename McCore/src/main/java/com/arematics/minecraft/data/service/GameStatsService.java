package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.GameStats;
import com.arematics.minecraft.data.mode.repository.GameStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class GameStatsService {

    private final GameStatsRepository repository;

    @Autowired
    public GameStatsService(GameStatsRepository gameStatsRepository){
        this.repository = gameStatsRepository;
    }

    @Cacheable(cacheNames = "playerStats")
    public GameStats findGameStats(UUID uuid){
        Optional<GameStats> gameStats = repository.findById(uuid);
        if(!gameStats.isPresent())
            throw new RuntimeException("GameStats for uuid: " + uuid.toString() + " could not be found");
        return gameStats.get();
    }

    @Cacheable(cacheNames = "playerStats")
    public GameStats getOrCreate(UUID uuid){
        try {
            return findGameStats(uuid);
        } catch (Exception exception) {
            return save(new GameStats(uuid, 0, 0, null));
        }
    }

    @CachePut(cacheNames = "playerStats")
    public GameStats save(GameStats stats){
        return repository.save(stats);
    }

    @CacheEvict(cacheNames = "playerStats")
    public void delete(GameStats stats){
        repository.delete(stats);
    }
}
