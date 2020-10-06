package com.arematics.minecraft.core.data.repository.chat;

import com.arematics.minecraft.core.data.model.placeholder.DynamicPlaceholder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DynamicPlaceholderRepository extends JpaRepository<DynamicPlaceholder, String> {
}
