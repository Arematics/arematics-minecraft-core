package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.global.model.Rank;
import com.arematics.minecraft.data.global.repository.RankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class RankService {

    private final RankRepository repository;
    private final RankPermissionService rankPermissionService;

    @Autowired
    public RankService(RankRepository rankRepository,
                       RankPermissionService rankPermissionService){
        this.repository = rankRepository;
        this.rankPermissionService = rankPermissionService;
    }

    public List<Rank> findAll(){
        return repository.findAll();
    }

    @Cacheable(cacheNames = "rankCache")
    public Rank getById(long id){
        Optional<Rank> rank = repository.findById(id);
        if(!rank.isPresent()) throw new RuntimeException("Rank with id: " + id + " not exists");
        return rank.get();
    }

    public Rank findByName(String name){
        Optional<Rank> rank = repository.findByName(name);
        if(!rank.isPresent()) throw new RuntimeException("Rank with name: " + name + " not exists");
        return rank.get();
    }

    @CachePut(cacheNames = "rankCache")
    public Rank getDefaultRank(){
        Optional<Rank> rank = repository.findByName("User");
        return rank.orElseGet(() -> repository.save(new Rank(1L, "User", "U",
                "§b", false, "g", new Timestamp(System.currentTimeMillis()))));
    }

    public boolean hasPermission(Rank rank, String permission){
        return rankPermissionService.hasPermission(rank, permission);
    }
}
