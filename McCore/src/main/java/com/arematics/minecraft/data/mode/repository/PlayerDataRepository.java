package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.PlayerData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface PlayerDataRepository extends JpaRepository<PlayerData, UUID>, JpaSpecificationExecutor<PlayerData> {

}
