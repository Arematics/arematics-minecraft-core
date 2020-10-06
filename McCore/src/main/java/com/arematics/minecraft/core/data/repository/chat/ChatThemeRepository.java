package com.arematics.minecraft.core.data.repository.chat;

import com.arematics.minecraft.core.data.model.theme.ChatTheme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatThemeRepository extends JpaRepository<ChatTheme, String> {
}
