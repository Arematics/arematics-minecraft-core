package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.global.model.PlayerVotes;
import com.arematics.minecraft.data.global.repository.PlayerVotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PlayerVotesService {

    private final PlayerVotesRepository repository;

    @Autowired
    public PlayerVotesService(PlayerVotesRepository playerVotesRepository){
        this.repository = playerVotesRepository;
    }

    @Cacheable(cacheNames = "player_votes", key = "#uuid")
    public PlayerVotes findPlayerVotes(UUID uuid){
        Optional<PlayerVotes> votes = repository.findById(uuid);
        if(!votes.isPresent())
            throw new RuntimeException("Votes for uuid: " + uuid.toString() + " could not be found");
        return votes.get();
    }

    @CachePut(cacheNames = "player_votes", key = "#result.uuid")
    public PlayerVotes updatePlayerVotes(PlayerVotes playerVotes){
        return repository.save(playerVotes);
    }

    @CacheEvict(cacheNames = "player_votes", key = "#playerVotes.uuid")
    public void removePlayerVotes(PlayerVotes playerVotes){
        repository.delete(playerVotes);
    }
}
