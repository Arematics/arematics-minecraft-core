package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.Kit;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface KitRepository extends PagingAndSortingRepository<Kit, Long>, JpaSpecificationExecutor<Kit> {
    Optional<Kit> findByName(String name);
}
