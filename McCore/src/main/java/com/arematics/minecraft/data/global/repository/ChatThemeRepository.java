package com.arematics.minecraft.data.global.repository;

import com.arematics.minecraft.data.global.model.ChatTheme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatThemeRepository extends JpaRepository<ChatTheme, String> {
}
