package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.Warp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarpRepository extends JpaRepository<Warp, String> {
}
