package com.arematics.minecraft.core.data.repository;

import com.arematics.minecraft.core.data.model.UserRankHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRankHistoryRepository extends JpaRepository<UserRankHistory, String>, JpaSpecificationExecutor<UserRankHistory> {

}
