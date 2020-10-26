package com.arematics.minecraft.data.repository.chat;

import com.arematics.minecraft.data.model.theme.ChatTheme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatThemeRepository extends JpaRepository<ChatTheme, String> {
}
