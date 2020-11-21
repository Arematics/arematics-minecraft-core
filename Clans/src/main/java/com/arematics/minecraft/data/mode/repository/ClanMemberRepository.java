package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.ClanMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ClanMemberRepository extends JpaRepository<ClanMember, UUID>, JpaSpecificationExecutor<ClanMember> {

}
