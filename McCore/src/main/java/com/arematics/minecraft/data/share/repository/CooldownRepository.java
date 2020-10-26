package com.arematics.minecraft.data.share.repository;

import com.arematics.minecraft.data.share.model.Cooldown;
import com.arematics.minecraft.data.share.model.CooldownKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CooldownRepository extends JpaRepository<Cooldown, CooldownKey> {
}
