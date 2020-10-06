package com.arematics.minecraft.core.data.repository.chat;

import com.arematics.minecraft.core.data.model.theme.ChatThemeUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatThemeUserRepository extends JpaRepository<ChatThemeUser, UUID> {
}
