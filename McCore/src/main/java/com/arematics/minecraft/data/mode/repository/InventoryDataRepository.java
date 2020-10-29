package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.InventoryData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventoryDataRepository extends JpaRepository<InventoryData, Long> {
    Optional<InventoryData> findByKey(String key);
    @Query("SELECT data.key FROM InventoryData data WHERE data.key LIKE %?1%")
    List<String> findAllKeysBy(String value);
}
