package com.arematics.minecraft.data.repository.chat;

import com.arematics.minecraft.data.model.placeholder.ThemePlaceholder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThemePlaceholderRepository extends JpaRepository<ThemePlaceholder, String> {
}
