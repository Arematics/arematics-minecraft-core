package com.arematics.minecraft.data.service;

import com.arematics.minecraft.core.chat.ChatAPI;
import com.arematics.minecraft.data.global.model.ChatTheme;
import com.arematics.minecraft.data.global.model.ChatThemeUser;
import com.arematics.minecraft.data.global.repository.ChatThemeUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ChatThemeUserService {

    private final ChatThemeUserRepository repository;

    @Autowired
    public ChatThemeUserService(ChatThemeUserRepository repository) {
        this.repository = repository;
    }

    @Cacheable(cacheNames = "chatThemeUserCache")
    public ChatThemeUser findById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    @CachePut(cacheNames = "chatThemeUserCache")
    public ChatThemeUser createUser(UUID id){
        ChatThemeUser user = new ChatThemeUser();
        user.setPlayerId(id);
        ChatTheme theme = ChatAPI.getTheme("default");
        user.setActiveTheme(theme);
        user = repository.save(user);
        return user;
    }

    public ChatThemeUser save(ChatThemeUser user) {
        return repository.save(user);
    }

    public ChatThemeUser getOrCreate(UUID id) {
        ChatThemeUser user = findById(id);
        if(null == user) {
            user = createUser(id);
        }
        return user;
    }

}
