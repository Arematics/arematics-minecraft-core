package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.AuctionCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionCategoryRepository extends JpaRepository<AuctionCategory, String> {
}
