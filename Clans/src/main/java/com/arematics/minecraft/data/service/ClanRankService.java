package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.ClanRank;
import com.arematics.minecraft.data.mode.model.ClanRankId;
import com.arematics.minecraft.data.mode.repository.ClanRanksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClanRankService {

    private final ClanRanksRepository repository;

    @Autowired
    public ClanRankService(ClanRanksRepository clanRanksRepository){
        this.repository = clanRanksRepository;
    }

    @Cacheable(cacheNames = "clanRanks")
    public ClanRank getClanRank(ClanRankId clanRankId){
        Optional<ClanRank> rank = repository.findById(clanRankId);
        if(!rank.isPresent())
            throw new RuntimeException("ClanRank for clan: " + clanRankId.getClanId() + " and name: " +
                    clanRankId.getName() + " could not be found");
        return rank.get();
    }

    @CachePut(cacheNames = "clanRanks")
    public ClanRank save(ClanRank clanRank){
        return repository.save(clanRank);
    }

    @CacheEvict(cacheNames = "clanRanks")
    public void delete(ClanRank clanRank){
        repository.delete(clanRank);
    }
}
