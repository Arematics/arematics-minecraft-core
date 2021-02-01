package com.arematics.minecraft.data.service;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.data.global.model.PlayerVotes;
import com.arematics.minecraft.data.global.repository.PlayerVotesRepository;
import org.bukkit.Bukkit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlayerVotesService {

    private final Map<Integer, Integer> votePointsList = new HashMap<>();
    private final PlayerVotesRepository repository;

    @Autowired
    public PlayerVotesService(PlayerVotesRepository playerVotesRepository){
        this.repository = playerVotesRepository;
        try{
            this.votePointsList.putAll(fetchVotePointList());
        }catch (Exception e){
            Bukkit.getLogger().severe("Vote Points map incorrect formed or not set");
        }
    }

    public Map<Integer, Integer> getVotePointsList() {
        return votePointsList;
    }

    @Cacheable(cacheNames = "player_votes", key = "#uuid")
    public PlayerVotes findPlayerVotes(UUID uuid){
        Optional<PlayerVotes> votes = repository.findById(uuid);
        if(!votes.isPresent())
            throw new RuntimeException("Votes for uuid: " + uuid.toString() + " could not be found");
        return votes.get();
    }

    @Cacheable(cacheNames = "player_votes")
    public PlayerVotes getOrCreate(UUID uuid){
        try {
            return findPlayerVotes(uuid);
        } catch (Exception exception) {
            return updatePlayerVotes(new PlayerVotes(uuid, 0, 0, 0, true, null));
        }
    }

    @CachePut(cacheNames = "player_votes", key = "#result.uuid")
    public PlayerVotes updatePlayerVotes(PlayerVotes playerVotes){
        return repository.save(playerVotes);
    }

    @CacheEvict(cacheNames = "player_votes", key = "#playerVotes.uuid")
    public void removePlayerVotes(PlayerVotes playerVotes){
        repository.delete(playerVotes);
    }

    public Map<Integer, Integer> fetchVotePointList() throws NumberFormatException {
        return Arrays.stream(fetchConfigValue())
                .map(value -> value.trim().split(":"))
                .collect(Collectors.toMap(data -> Integer.parseInt(data[0]), data -> Integer.parseInt(data[1])));
    }

    public String[] fetchConfigValue(){
        return Boots.getBoot(CoreBoot.class).getPluginConfig().findByKey("vote_point_list").split(",");
    }
}
