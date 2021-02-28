package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.global.model.ChatFilterMessage;
import com.arematics.minecraft.data.global.repository.ChatFilterMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "chatMessage", cacheManager = "globalCache")
public class ChatFilterService {
    private final ChatFilterMessageRepository chatFilterMessageRepository;
    private final List<String> blocked;

    @Autowired
    public ChatFilterService(ChatFilterMessageRepository chatFilterMessageRepository){
        this.chatFilterMessageRepository = chatFilterMessageRepository;
        blocked = this.chatFilterMessageRepository.findAll().stream()
                .map(ChatFilterMessage::getChatFilter)
                .collect(Collectors.toList());
    }

    public List<String> getBlocked() {
        return blocked;
    }

    @Cacheable(key = "#message")
    public ChatFilterMessage isInFilter(String message){
        Optional<ChatFilterMessage> result = chatFilterMessageRepository.findById(message);
        if(!result.isPresent())
            throw new RuntimeException("Chat Filter for message: " + message + " could not be found");
        return result.get();
    }
}
