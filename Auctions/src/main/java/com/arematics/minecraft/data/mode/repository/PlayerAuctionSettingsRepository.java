package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.PlayerAuctionSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlayerAuctionSettingsRepository extends JpaRepository<PlayerAuctionSettings, UUID> {
}
