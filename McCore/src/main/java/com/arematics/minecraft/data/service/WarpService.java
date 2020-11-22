package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.Warp;
import com.arematics.minecraft.data.mode.repository.WarpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WarpService {

    private final WarpRepository warpRepository;

    @Autowired
    public WarpService(WarpRepository warpRepository) { this.warpRepository = warpRepository; }

    //save

    public Warp saveWarp(Warp warp){
        return warpRepository.save(warp);
    }

    //update

    public Warp updateWarp(Warp warp){
        return warpRepository.save(warp);
    }

    //delete
    public void deleteWarp(Warp warp) {
        warpRepository.delete(warp);
    }
    //get
    public Warp getWarp(String name) {
        Optional<Warp> warp = warpRepository.findById(name);
        return warp.orElseThrow(() -> new RuntimeException("Warp with name: " + name + " could not be found"));
    }

}
