package com.arematics.minecraft.data.global.repository;

import com.arematics.minecraft.data.global.model.Warn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface WarnsRepository extends JpaRepository<Warn, Long>, JpaSpecificationExecutor<Warn> {
    List<Warn> findAllByUuidOrderByWarnedTimeDesc(UUID uuid);
    @Query("select sum(warn.amount) FROM Warn warn WHERE uuid = :uuid")
    Integer countByAmountOfUuid(@Param("uuid") UUID uuid);
}
