package com.arematics.minecraft.data.share.repository;

import com.arematics.minecraft.data.share.model.UserPermId;
import com.arematics.minecraft.data.share.model.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.UUID;

public interface UserPermissionRepository extends JpaRepository<UserPermission, UserPermId>, JpaSpecificationExecutor<UserPermission> {
    @Query("select case when count(perm) > 0 then true else false end from " +
            "UserPermission perm where perm.uuid = :id and perm.permission = :permission" +
            " and (perm.until IS null OR perm.until > :time)")
    boolean hasPerm(@Param("id") UUID id, @Param("permission") String permission, @Param("time") Timestamp time);
}
