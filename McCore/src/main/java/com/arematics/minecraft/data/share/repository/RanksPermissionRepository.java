package com.arematics.minecraft.data.share.repository;

import com.arematics.minecraft.data.share.model.RankPermId;
import com.arematics.minecraft.data.share.model.RanksPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.sql.Timestamp;

public interface RanksPermissionRepository extends JpaRepository<RanksPermission, RankPermId>, JpaSpecificationExecutor<RanksPermission> {
    boolean existsByIdAndPermissionAndUntilAfter(Long id, String permission, Timestamp time);
}
