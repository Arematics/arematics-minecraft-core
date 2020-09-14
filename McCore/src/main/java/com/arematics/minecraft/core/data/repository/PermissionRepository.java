package com.arematics.minecraft.core.data.repository;

import com.arematics.minecraft.core.data.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PermissionRepository extends JpaRepository<Permission, String>, JpaSpecificationExecutor<Permission> {

}
