package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.PlayerData;
import com.arematics.minecraft.data.mode.repository.PlayerDataRepository;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = "playerdata")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PlayerDataService implements ModeMessageReceiveService {
    private final PlayerDataRepository playerdatarepository;

    @Cacheable(key = "#id")
    public PlayerData findPlayerData(UUID id) {
        Optional<PlayerData> result = playerdatarepository.findById(id);
        if (!result.isPresent())
            throw new RuntimeException("Change this");
        return result.get();
    }

    @CachePut(key = "#result.uuid")
    public PlayerData save(PlayerData playerdata) {
        return playerdatarepository.save(playerdata);
    }

    @CacheEvict(key = "#playerdata.uuid")
    public void remove(PlayerData playerdata) {
        playerdatarepository.delete(playerdata);
    }

    @Override
    public String messageKey() {
        return "playerdata";
    }

    @Override
    public void onReceive(String data) {
        try{
            UUID uuid = UUID.fromString(data);
            Player player = Bukkit.getPlayer(uuid);
            if(player != null){
                PlayerData playerData = findPlayerData(uuid);
                player.setHealth(playerData.getHealth());
                player.setFoodLevel(playerData.getHunger());
                player.setLevel(playerData.getLevel());
                player.setExp(playerData.getXp());
            }
        }catch (Exception ignore){}
    }
}
