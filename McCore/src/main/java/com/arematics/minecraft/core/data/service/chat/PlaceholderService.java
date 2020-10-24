package com.arematics.minecraft.core.data.service.chat;

import com.arematics.minecraft.core.data.model.placeholder.DynamicPlaceholder;
import com.arematics.minecraft.core.data.model.placeholder.ThemePlaceholder;
import com.arematics.minecraft.core.data.repository.chat.DynamicPlaceholderRepository;
import com.arematics.minecraft.core.data.repository.chat.ThemePlaceholderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceholderService {

    private final DynamicPlaceholderRepository dynamicRepository;
    private final ThemePlaceholderRepository themeRepository;

    @Autowired
    public PlaceholderService(DynamicPlaceholderRepository dynamicRepository, ThemePlaceholderRepository themeRepository) {
        this.dynamicRepository = dynamicRepository;
        this.themeRepository = themeRepository;
    }

    public List<DynamicPlaceholder> loadGlobals(){
        return dynamicRepository.findAll();
    }

    public DynamicPlaceholder getGlobalPlaceholder(String placeholderKey) {
        return dynamicRepository.findById(placeholderKey).orElse(null);
    }

    public DynamicPlaceholder save(DynamicPlaceholder dynamicPlaceholder){
        return dynamicRepository.save(dynamicPlaceholder);
    }

    public ThemePlaceholder getThemePlaceholder(String placeholderKey) {
        return themeRepository.findById(placeholderKey).orElse(null);
    }

    public ThemePlaceholder save(ThemePlaceholder themePlaceholder) {
        return themeRepository.save(themePlaceholder);
    }

}
