package com.arematics.minecraft.core.data.repository;

import com.arematics.minecraft.core.data.model.ArematicsAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ArematicsAccountRepository extends JpaRepository<ArematicsAccount, UUID> {
    Optional<ArematicsAccount> findArematicsAccountBySoulConnection(UUID soulConnection);
}
