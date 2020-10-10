package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.Kit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface KitRepository extends JpaRepository<Kit, Long>, JpaSpecificationExecutor<Kit> {
    Optional<Kit> findByName(String name);
}
