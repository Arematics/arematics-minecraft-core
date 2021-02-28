package com.arematics.minecraft.data.global.repository;

import com.arematics.minecraft.data.global.model.ChatFilterMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatFilterMessageRepository extends JpaRepository<ChatFilterMessage, String> {
}
