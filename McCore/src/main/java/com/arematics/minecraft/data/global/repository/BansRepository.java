package com.arematics.minecraft.data.global.repository;

import com.arematics.minecraft.data.global.model.Ban;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface BansRepository extends JpaRepository<Ban, UUID>, JpaSpecificationExecutor<Ban> {

}
