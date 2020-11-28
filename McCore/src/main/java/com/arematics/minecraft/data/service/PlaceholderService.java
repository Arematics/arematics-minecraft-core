package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.global.model.GlobalPlaceholder;
import com.arematics.minecraft.data.global.model.ThemePlaceholder;
import com.arematics.minecraft.data.global.repository.GlobalPlaceholderRepository;
import com.arematics.minecraft.data.global.repository.ThemePlaceholderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceholderService {

    private final GlobalPlaceholderRepository globalPlaceholderRepository;
    private final ThemePlaceholderRepository themeRepository;

    @Autowired
    public PlaceholderService(GlobalPlaceholderRepository globalPlaceholderRepository, ThemePlaceholderRepository themeRepository) {
        this.globalPlaceholderRepository = globalPlaceholderRepository;
        this.themeRepository = themeRepository;
    }

    public List<GlobalPlaceholder> loadGlobals(){
        return globalPlaceholderRepository.findAll();
    }

    public GlobalPlaceholder save(GlobalPlaceholder globalPlaceholder) {
        return globalPlaceholderRepository.save(globalPlaceholder);
    }

    public ThemePlaceholder save(ThemePlaceholder themePlaceholder) {
        return themeRepository.save(themePlaceholder);
    }

}
