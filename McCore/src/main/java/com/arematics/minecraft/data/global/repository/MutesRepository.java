package com.arematics.minecraft.data.global.repository;

import com.arematics.minecraft.data.global.model.Mute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface MutesRepository extends JpaRepository<Mute, UUID>, JpaSpecificationExecutor<Mute> {

}
