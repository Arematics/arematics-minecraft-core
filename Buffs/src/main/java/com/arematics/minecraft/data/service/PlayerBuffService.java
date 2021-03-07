package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.PlayerBuff;
import com.arematics.minecraft.data.mode.model.PlayerBuffId;
import com.arematics.minecraft.data.mode.repository.PlayerBuffRepository;
import lombok.RequiredArgsConstructor;
import org.bukkit.potion.PotionEffectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class PlayerBuffService {
    private final PlayerBuffRepository playerBuffRepository;

    public boolean hasBuff(UUID uuid, PotionEffectType effectType){
        return playerBuffRepository.existsById(new PlayerBuffId(uuid, effectType.getName()));
    }

    public PlayerBuff findBuff(UUID uuid, PotionEffectType effectType){
        PlayerBuffId id = new PlayerBuffId(uuid, effectType.getName());
        Optional<PlayerBuff> buff = playerBuffRepository.findById(id);
        if(!buff.isPresent())
            throw new RuntimeException("Buff for uuid: " + uuid.toString() + " of type: " + effectType.getName() + " could not be found");
        return buff.get();
    }

    public List<PlayerBuff> findActiveBuffsByPlayer(UUID uuid){
        return playerBuffRepository.findAllByIdAndActiveIsTrueAndEndTimeAfter(uuid, Timestamp.valueOf(LocalDateTime.now()));
    }

    public List<PlayerBuff> findValidBuffsByPlayer(UUID uuid){
        return playerBuffRepository.findAllByIdAndEndTimeAfter(uuid, Timestamp.valueOf(LocalDateTime.now()));
    }

    public List<PlayerBuff> findBuffsByPlayer(UUID uuid){
        return playerBuffRepository.findAllById(uuid);
    }

    public PlayerBuff update(PlayerBuff buff){
        return playerBuffRepository.save(buff);
    }

    public void delete(PlayerBuff buff){
        playerBuffRepository.delete(buff);
    }
}
