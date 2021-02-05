package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.PlayerBuff;
import com.arematics.minecraft.data.mode.model.PlayerBuffId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface PlayerBuffRepository extends JpaRepository<PlayerBuff, PlayerBuffId> {
    List<PlayerBuff> findAllById(UUID uuid);
    List<PlayerBuff> findAllByIdAndEndTimeAfter(UUID uuid, Timestamp timestamp);
}
