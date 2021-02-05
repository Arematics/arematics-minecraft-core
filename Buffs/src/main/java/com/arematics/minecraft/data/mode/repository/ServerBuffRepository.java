package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.ServerBuff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerBuffRepository extends JpaRepository<ServerBuff, String> {
}
