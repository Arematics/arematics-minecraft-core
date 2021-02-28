package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.Warp;
import com.arematics.minecraft.data.mode.repository.WarpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = "warps")
public class WarpService {

    private final WarpRepository warpRepository;

    @Autowired
    public WarpService(WarpRepository warpRepository) { this.warpRepository = warpRepository; }

    public List<Warp> fetchAllWarps(){
        return warpRepository.findAll();
    }

    @Cacheable(key = "#name")
    public Warp getWarp(String name) {
        Optional<Warp> warp = warpRepository.findById(name);
        return warp.orElseThrow(() -> new RuntimeException("Warp with name: " + name + " could not be found"));
    }

    @CachePut(key = "#result.name")
    public Warp saveWarp(Warp warp){
        return warpRepository.save(warp);
    }

    @CacheEvict(key = "#warp.name")
    public void deleteWarp(Warp warp) {
        warpRepository.delete(warp);
    }

}
