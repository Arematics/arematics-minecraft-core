package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.AuctionSort;
import com.arematics.minecraft.data.mode.model.AuctionType;
import com.arematics.minecraft.data.mode.model.PlayerAuctionSettings;
import com.arematics.minecraft.data.mode.repository.PlayerAuctionSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = "player_auction_settings")
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class PlayerAuctionSettingsService {
    private final PlayerAuctionSettingsRepository playerAuctionSettingsRepository;

    @CachePut(key = "#result.uuid")
    public PlayerAuctionSettings findOrCreateDefault(UUID uuid){
        try{
            return findById(uuid);
        }catch (RuntimeException re) {
            return save(new PlayerAuctionSettings(uuid, null, AuctionType.ALL, AuctionSort.HIGHEST_BID, ""));
        }
    }

    @Cacheable(key = "#uuid")
    public PlayerAuctionSettings findById(UUID uuid){
        Optional<PlayerAuctionSettings> result = playerAuctionSettingsRepository.findById(uuid);
        if(!result.isPresent())
            throw new RuntimeException("Could not find auction settings for uuid: " + uuid.toString());
        return result.get();
    }

    @CachePut(key = "#result.uuid")
    public PlayerAuctionSettings save(PlayerAuctionSettings settings){
        return playerAuctionSettingsRepository.save(settings);
    }

    @CacheEvict(key = "#settings.uuid")
    public void delete(PlayerAuctionSettings settings){
        playerAuctionSettingsRepository.delete(settings);
    }
}
