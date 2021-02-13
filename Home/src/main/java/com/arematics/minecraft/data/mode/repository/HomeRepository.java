package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.Home;
import com.arematics.minecraft.data.mode.model.HomeId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface HomeRepository extends PagingAndSortingRepository<Home, HomeId> {
    Page<Home> findAllByOwnerAndNameStartsWithOrderByName(UUID owner, String startsWith, Pageable pageable);
    Page<Home> findAllByOwnerOrderByName(UUID owner, Pageable pageable);
}
