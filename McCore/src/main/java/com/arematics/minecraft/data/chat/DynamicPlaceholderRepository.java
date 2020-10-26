package com.arematics.minecraft.data.chat;

import com.arematics.minecraft.data.chat.placeholder.GlobalPlaceholder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DynamicPlaceholderRepository extends JpaRepository<GlobalPlaceholder, String> {
}
