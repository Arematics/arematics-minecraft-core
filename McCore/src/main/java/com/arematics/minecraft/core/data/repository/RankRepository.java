package com.arematics.minecraft.core.data.repository;

import com.arematics.minecraft.core.data.model.Rank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RankRepository extends JpaRepository<Rank, Long>, JpaSpecificationExecutor<Rank> {
    Optional<Rank> findByName(String name);
}
