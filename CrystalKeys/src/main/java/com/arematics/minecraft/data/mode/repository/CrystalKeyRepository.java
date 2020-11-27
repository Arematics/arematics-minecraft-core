package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.CrystalKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CrystalKeyRepository extends JpaRepository<CrystalKey, String> {
    @Query("SELECT key.name FROM CrystalKey key")
    List<String> findAllNames();
}
