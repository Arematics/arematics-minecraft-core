package com.arematics.minecraft.data.global.repository;

import com.arematics.minecraft.data.global.model.SoulOg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SoulOgRepository extends JpaRepository<SoulOg, UUID>, JpaSpecificationExecutor<SoulOg> {

}
