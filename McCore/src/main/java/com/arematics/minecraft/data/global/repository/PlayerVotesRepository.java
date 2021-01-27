package com.arematics.minecraft.data.global.repository;

import com.arematics.minecraft.data.global.model.PlayerVotes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlayerVotesRepository extends JpaRepository<PlayerVotes, UUID> {
}
