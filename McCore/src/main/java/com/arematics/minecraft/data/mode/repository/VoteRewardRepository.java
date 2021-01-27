package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.VoteReward;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRewardRepository extends JpaRepository<VoteReward, String> {
}
