package com.arematics.minecraft.data.global.repository;

import com.arematics.minecraft.core.data.model.Bans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BansRepository extends JpaRepository<Bans, String>, JpaSpecificationExecutor<Bans> {

}