package com.arematics.minecraft.data.chat;

import com.arematics.minecraft.data.chat.placeholder.ThemePlaceholder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThemePlaceholderRepository extends JpaRepository<ThemePlaceholder, String> {
}
