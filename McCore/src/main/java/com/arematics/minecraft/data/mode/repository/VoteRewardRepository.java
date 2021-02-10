package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.VoteReward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteRewardRepository extends JpaRepository<VoteReward, String> {
    List<VoteReward> findAllByOrderByCosts();
}
