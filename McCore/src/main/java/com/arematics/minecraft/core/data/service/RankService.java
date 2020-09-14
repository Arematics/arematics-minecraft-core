package com.arematics.minecraft.core.data.service;

import com.arematics.minecraft.core.data.model.Rank;
import com.arematics.minecraft.core.data.repository.RankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class RankService {

    private final RankRepository repository;

    @Autowired
    public RankService(RankRepository rankRepository){
        this.repository = rankRepository;
    }

    @Cacheable(cacheNames = "rankCache")
    public Rank getById(long id){
        Optional<Rank> rank = repository.findById(id);
        if(!rank.isPresent()) throw new RuntimeException("Rank with id: " + id + " not exists");
        return rank.get();
    }

    @CachePut(cacheNames = "rankCache")
    public Rank getDefaultRank(){
        Optional<Rank> rank = repository.findById(1L);
        return rank.orElseGet(() -> repository.save(new Rank(1L, "User", "U", "§b",
                new Timestamp(System.currentTimeMillis()), new HashSet<>())));
    }
}
