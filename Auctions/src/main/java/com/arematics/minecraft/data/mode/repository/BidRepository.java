package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.Bid;
import com.arematics.minecraft.data.mode.model.BidId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, BidId> {
}
