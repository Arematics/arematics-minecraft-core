package com.arematics.minecraft.data.global.repository;

import com.arematics.minecraft.data.global.model.ChatThemeUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatThemeUserRepository extends JpaRepository<ChatThemeUser, UUID> {
}
