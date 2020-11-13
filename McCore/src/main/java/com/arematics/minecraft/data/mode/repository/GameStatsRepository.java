package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.GameStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameStatsRepository extends JpaRepository<GameStats, UUID> {
}
