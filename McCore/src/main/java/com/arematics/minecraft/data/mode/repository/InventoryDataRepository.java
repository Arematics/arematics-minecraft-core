package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.InventoryData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryDataRepository extends JpaRepository<InventoryData, Long> {
    Optional<InventoryData> findByDataKey(String dataKey);
    @Query("SELECT data.editBlocked FROM InventoryData data WHERE data.dataKey = dataKey")
    boolean isEditBlocked(@Param("dataKey") String dataKey);
    @Query("SELECT data.dataKey FROM InventoryData data")
    List<String> findDataKeys();
    @Query("SELECT data.dataKey FROM InventoryData data WHERE data.dataKey LIKE %?1%")
    List<String> findDataKeys(String startsWith);
}
