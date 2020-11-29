package com.arematics.minecraft.data.share.repository;

import com.arematics.minecraft.data.share.model.BroadcastMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BroadcastMessageRepository extends JpaRepository<BroadcastMessage, Long> {
}
