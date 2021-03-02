package com.arematics.minecraft.data.global.repository;

import com.arematics.minecraft.data.global.model.AccountLink;
import com.arematics.minecraft.data.global.model.AccountLinkId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountLinkRepository extends JpaRepository<AccountLink, AccountLinkId> {
    boolean existsAccountLinkByUserOneAndUserTwo(UUID userOne, UUID userTwo);
}
