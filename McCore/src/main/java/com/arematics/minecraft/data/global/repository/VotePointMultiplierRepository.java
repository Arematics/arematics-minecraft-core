package com.arematics.minecraft.data.global.repository;

import com.arematics.minecraft.data.global.model.VotePointMultiplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VotePointMultiplierRepository extends JpaRepository<VotePointMultiplier, Integer> {
    Optional<VotePointMultiplier> findVotePointMultiplierByStreakIsLessThan(Integer streak);
}
