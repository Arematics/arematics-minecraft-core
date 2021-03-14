package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.BuffListMode;
import com.arematics.minecraft.data.mode.model.PlayerBuff;
import com.arematics.minecraft.data.mode.model.PlayerBuffId;
import com.arematics.minecraft.data.mode.repository.PlayerBuffRepository;
import lombok.RequiredArgsConstructor;
import org.bukkit.potion.PotionEffectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

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


    public PlayerBuff findValidBuff(UUID uuid, PotionEffectType effectType){
        Optional<PlayerBuff> buff = playerBuffRepository.findByValidBuff(uuid,
                effectType.getName(),
                Timestamp.valueOf(LocalDateTime.now()));
        if(!buff.isPresent())
            throw new RuntimeException("Buff for uuid: " + uuid.toString() + " of type: " + effectType.getName() + " could not be found");
        return buff.get();
    }

    public List<PlayerBuff> findAllActiveBuffsByPlayer(UUID uuid){
        return playerBuffRepository.findAllByIdAndActiveIsTrueAndEndTimeIsAfterOrEndTimeIsNullAndActiveIsTrue(uuid,
                Timestamp.valueOf(LocalDateTime.now()));
    }

    public Page<PlayerBuff> findBuffsByPlayer(UUID uuid, int page, Supplier<BuffListMode> modeSupplier){
        BuffListMode listMode = modeSupplier.get();
        switch (listMode){
            case TIMED: return findTimedBuffsByPlayer(uuid, page);
            case PERMANENT: return findPermanentBuffsByPlayer(uuid, page);
            case ACTIVE: return findBuffsByPlayer(uuid, page, true);
            case INACTIVE: return findBuffsByPlayer(uuid, page, false);
            default: return findValidBuffsByPlayer(uuid, page);
        }
    }

    private Page<PlayerBuff> findValidBuffsByPlayer(UUID uuid, int page){
        return playerBuffRepository.findAllByIdAndEndTimeIsAfterOrEndTimeIsNull(uuid,
                Timestamp.valueOf(LocalDateTime.now()),
                PageRequest.of(page, 7));
    }

    private Page<PlayerBuff> findBuffsByPlayer(UUID uuid, int page, boolean active){
        return playerBuffRepository.findAllByActive(uuid,
                active,
                Timestamp.valueOf(LocalDateTime.now()),
                PageRequest.of(page, 7));
    }

    private Page<PlayerBuff> findTimedBuffsByPlayer(UUID uuid, int page){
        return playerBuffRepository.findAllByIdAndEndTimeIsAfter(uuid,
                Timestamp.valueOf(LocalDateTime.now()),
                PageRequest.of(page, 7));
    }

    private Page<PlayerBuff> findPermanentBuffsByPlayer(UUID uuid, int page){
        return playerBuffRepository.findAllByIdAndEndTimeIsNull(uuid,
                PageRequest.of(page, 7));
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
