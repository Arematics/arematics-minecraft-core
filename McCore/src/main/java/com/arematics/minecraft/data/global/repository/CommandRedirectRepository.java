package com.arematics.minecraft.data.global.repository;

import com.arematics.minecraft.data.global.model.CommandRedirect;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandRedirectRepository extends JpaRepository<CommandRedirect, String> {
}
