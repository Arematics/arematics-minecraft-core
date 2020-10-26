package com.arematics.minecraft.data.repository.chat;

import com.arematics.minecraft.data.model.placeholder.GlobalPlaceholder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DynamicPlaceholderRepository extends JpaRepository<GlobalPlaceholder, String> {
}
