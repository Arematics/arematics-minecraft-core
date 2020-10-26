package com.arematics.minecraft.data.chat;

import com.arematics.minecraft.data.chat.theme.ChatTheme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatThemeRepository extends JpaRepository<ChatTheme, String> {
}
