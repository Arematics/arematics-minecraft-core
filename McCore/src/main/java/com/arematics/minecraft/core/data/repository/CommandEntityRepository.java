package com.arematics.minecraft.core.data.repository;

import com.arematics.minecraft.core.data.model.CommandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface CommandEntityRepository  extends JpaRepository<CommandEntity, UUID>, JpaSpecificationExecutor<CommandEntity> {
}
