package com.arematics.minecraft.data.share.repository;

import com.arematics.minecraft.data.share.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PermissionRepository extends JpaRepository<Permission, String>, JpaSpecificationExecutor<Permission> {

}