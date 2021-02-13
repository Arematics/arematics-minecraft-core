package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.Home;
import com.arematics.minecraft.data.mode.model.HomeId;
import com.arematics.minecraft.data.mode.repository.HomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "homes")
public class HomeService {
    private final HomeRepository homeRepository;

    public Page<Home> findAllByOwnerAndSearch(UUID owner, String startsWith, int page){
        Pageable pageable = PageRequest.of(page, 28);
        return homeRepository.findAllByOwnerAndNameStartsWithOrderByName(owner, startsWith, pageable);
    }

    public Page<Home> findAllByOwner(UUID owner, int page){
        Pageable pageable = PageRequest.of(page, 28);
        return homeRepository.findAllByOwnerOrderByName(owner, pageable);
    }

    @Cacheable(key = "#id.owner + #id.name")
    public Home findByOwnerAndName(HomeId id){
        Optional<Home> result = homeRepository.findById(id);
        if(!result.isPresent())
            throw new RuntimeException("Home with id: " + id.getName() + " could not be found for player");
        return result.get();
    }

    @CachePut(key = "#result.owner + #result.name")
    public Home save(Home home){
        return homeRepository.save(home);
    }

    @CacheEvict(key = "#home.owner + #home.name")
    public void delete(Home home){
        homeRepository.delete(home);
    }
}
