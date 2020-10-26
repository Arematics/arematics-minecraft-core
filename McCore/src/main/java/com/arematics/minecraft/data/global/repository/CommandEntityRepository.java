package com.arematics.minecraft.data.global.repository;

import com.arematics.minecraft.data.global.model.CommandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface CommandEntityRepository  extends JpaRepository<CommandEntity, UUID>, JpaSpecificationExecutor<CommandEntity> {
}
