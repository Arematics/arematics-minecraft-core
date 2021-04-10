package com.arematics.minecraft.data.share.repository;

import com.arematics.minecraft.data.share.model.RankPermId;
import com.arematics.minecraft.data.share.model.RanksPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface RanksPermissionRepository extends JpaRepository<RanksPermission, RankPermId>, JpaSpecificationExecutor<RanksPermission> {
    @Query("select case when count(perm) > 0 then true else false end from " +
            "RanksPermission perm where perm.id = :id and perm.permission = :permission" +
            " and (perm.until IS null OR perm.until > :time)")
    boolean hasPerm(@Param("id") Long id, @Param("permission") String permission, @Param("time") Timestamp time);
    @Query("select perm.permission from RanksPermission perm WHERE perm.id = :id")
    List<String> listAllPermsByRank(@Param("id") Long id);
}
