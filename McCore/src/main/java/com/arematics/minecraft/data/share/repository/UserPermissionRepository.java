package com.arematics.minecraft.data.share.repository;

import com.arematics.minecraft.data.share.model.UserPermId;
import com.arematics.minecraft.data.share.model.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.sql.Timestamp;
import java.util.UUID;

public interface UserPermissionRepository extends JpaRepository<UserPermission, UserPermId>, JpaSpecificationExecutor<UserPermission> {
    boolean existsByUuidAndPermissionAndUntilAfter(UUID uuid, String permission, Timestamp time);
}
