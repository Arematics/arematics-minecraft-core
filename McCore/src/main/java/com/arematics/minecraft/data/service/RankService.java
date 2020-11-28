package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.global.model.Rank;
import com.arematics.minecraft.data.global.repository.RankRepository;
import com.arematics.minecraft.data.share.repository.PermissionRepository;
import org.bukkit.Bukkit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Optional;

@Service
public class RankService {

    @Value("${mode.name}")
    private String modeName;

    private final RankRepository repository;
    private final PermissionRepository permissionRepository;

    @Autowired
    public RankService(RankRepository rankRepository, PermissionRepository permissionRepository){
        this.repository = rankRepository;
        this.permissionRepository = permissionRepository;
    }

    @Cacheable(cacheNames = "rankCache")
    public Rank getById(long id){
        Optional<Rank> rank = repository.findById(id);
        if(!rank.isPresent()) throw new RuntimeException("Rank with id: " + id + " not exists");
        Rank entity = rank.get();
        entity.getPermissions().addAll(permissionRepository.findAllByRankIdAndMode(id, modeName));
        return rank.get();
    }

    @CachePut(cacheNames = "rankCache")
    public Rank getDefaultRank(){
        Optional<Rank> rank = repository.findById(1L);
        return rank.orElseGet(() -> repository.save(new Rank(1L, "User", "U",
                "§b", false, new Timestamp(System.currentTimeMillis()), new HashSet<>())));
    }
}
