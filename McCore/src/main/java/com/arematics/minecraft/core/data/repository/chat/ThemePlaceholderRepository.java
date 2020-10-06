package com.arematics.minecraft.core.data.repository.chat;

import com.arematics.minecraft.core.data.model.placeholder.ThemePlaceholder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThemePlaceholderRepository extends JpaRepository<ThemePlaceholder, String> {
}
