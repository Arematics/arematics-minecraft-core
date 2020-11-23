package com.arematics.minecraft.data.share.repository;

import com.arematics.minecraft.data.share.model.OnlineTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OnlineTimeRepository extends JpaRepository<OnlineTime, UUID> {
}
