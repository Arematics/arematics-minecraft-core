package com.arematics.minecraft.data.global.repository;

import com.arematics.minecraft.data.global.model.GlobalPlaceholder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GlobalPlaceholderRepository extends JpaRepository<GlobalPlaceholder, String> {
}
