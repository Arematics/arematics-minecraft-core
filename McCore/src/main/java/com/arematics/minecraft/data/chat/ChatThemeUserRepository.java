package com.arematics.minecraft.data.chat;

import com.arematics.minecraft.data.chat.theme.ChatThemeUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatThemeUserRepository extends JpaRepository<ChatThemeUser, UUID> {
}
