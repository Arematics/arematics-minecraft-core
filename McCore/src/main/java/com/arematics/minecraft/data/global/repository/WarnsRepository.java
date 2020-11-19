package com.arematics.minecraft.data.global.repository;

import com.arematics.minecraft.data.global.model.Warn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface WarnsRepository extends JpaRepository<Warn, UUID>, JpaSpecificationExecutor<Warn> {
    List<Warn> findAllByUuidOrderByWarnedTimeDesc(UUID uuid);
}
