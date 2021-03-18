package com.arematics.minecraft.data.global.repository;

import com.arematics.minecraft.data.global.model.Friend;
import com.arematics.minecraft.data.global.model.FriendId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface FriendRepository extends JpaRepository<Friend, FriendId>, JpaSpecificationExecutor<Friend> {
    Page<Friend> findAllByUuidOrTargetUuid(UUID uuid, UUID targetUuid, Pageable pageable);
    Optional<Friend> findByUuidAndTargetUuid(UUID uuid, UUID targetUuid);
    boolean existsByUuidAndTargetUuid(UUID uuid, UUID targetUuid);
    void deleteByUuidAndTargetUuid(UUID uuid, UUID targetUuid);
}
