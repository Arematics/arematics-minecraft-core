package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.ClanRank;
import com.arematics.minecraft.data.mode.model.ClanRankId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClanRanksRepository extends JpaRepository<ClanRank, ClanRankId>, JpaSpecificationExecutor<ClanRank> {

}
