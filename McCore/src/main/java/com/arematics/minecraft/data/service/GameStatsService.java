package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.GameStats;
import com.arematics.minecraft.data.mode.repository.GameStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = "playerStats")
public class GameStatsService {

    private final GameStatsRepository repository;

    @Autowired
    public GameStatsService(GameStatsRepository gameStatsRepository){
        this.repository = gameStatsRepository;
    }

    public Page<GameStats> findTopTenBy(Sort sort){
        return repository.findAllBy(PageRequest.of(0, 10, sort));
    }

    @Cacheable(key = "#uuid")
    public GameStats findGameStats(UUID uuid){
        Optional<GameStats> gameStats = repository.findById(uuid);
        if(!gameStats.isPresent())
            throw new RuntimeException("GameStats for uuid: " + uuid.toString() + " could not be found");
        return gameStats.get();
    }

    @Cacheable(key = "#uuid")
    public GameStats getOrCreate(UUID uuid){
        try {
            return findGameStats(uuid);
        } catch (Exception exception) {
            return save(new GameStats(uuid, 0, 0, null, .0));
        }
    }

    @CachePut(key = "#result.uuid")
    public GameStats save(GameStats stats){
        return repository.save(stats);
    }

    @CacheEvict(key = "#stats.uuid")
    public void delete(GameStats stats){
        repository.delete(stats);
    }
}
