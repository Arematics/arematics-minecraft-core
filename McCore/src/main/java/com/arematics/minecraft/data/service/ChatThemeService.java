package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.global.model.ChatTheme;
import com.arematics.minecraft.data.global.repository.ChatThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatThemeService {

    private final ChatThemeRepository repository;

    @Autowired
    public ChatThemeService(ChatThemeRepository repository) {
        this.repository = repository;
    }

    public Optional<ChatTheme> findById(String themeId) {
        return repository.findById(themeId);
    }

    public List<ChatTheme> getAll() {
        return repository.findAll();
    }

    public ChatTheme get(String themeKey) {
        return findById(themeKey).orElse(null);
    }

    public ChatTheme save(ChatTheme theme) {
        return repository.save(theme);
    }

}
