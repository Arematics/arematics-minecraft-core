package com.arematics.minecraft.core.data.repository;

import com.arematics.minecraft.core.data.model.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface UserPermissionRepository extends JpaRepository<UserPermission, UUID>, JpaSpecificationExecutor<UserPermission> {

}
