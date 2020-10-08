package com.arematics.minecraft.data.share.repository;

import com.arematics.minecraft.data.share.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, String>, JpaSpecificationExecutor<Permission> {
    @Query(value = "SELECT * from soulpvp.permission p " +
            "INNER JOIN soulpvp.ranks_permission ON p.permission = ranks_permission.permission " +
            "WHERE id = ?1 AND mode = ?2", nativeQuery = true)
    List<Permission> findAllByRankIdAndMode(Long id, String mode);
    @Query(value = "SELECT * from soulpvp.permission p " +
            "INNER JOIN soulpvp.user_permission ON p.permission = user_permission.permission " +
            "WHERE uuid = ?1 AND mode = ?2", nativeQuery = true)
    List<Permission> findAllByUserUUIDAndMode(String uuid, String mode);
}
