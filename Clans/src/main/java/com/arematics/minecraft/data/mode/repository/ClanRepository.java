package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.Clan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ClanRepository extends JpaRepository<Clan, Long>, JpaSpecificationExecutor<Clan> {
    Optional<Clan> findByName(String name);
    Optional<Clan> findByTag(String tag);
}
