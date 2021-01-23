package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.GameMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MapRepository extends JpaRepository<GameMap, String>, JpaSpecificationExecutor<GameMap> {
    @Query("select map.id from GameMap map")
    List<String> findAllIds();
}
