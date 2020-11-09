package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.Kit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface KitRepository extends JpaRepository<Kit, Long>, JpaSpecificationExecutor<Kit> {
    String FIND_NAMES = "SELECT name FROM kit";

    Optional<Kit> findByName(String name);
    @Query(value = FIND_NAMES, nativeQuery = true)
    List<String> findNames();
}
