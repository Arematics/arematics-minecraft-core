package com.arematics.minecraft.core.chat.controller;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.chat.ChatAPI;
import com.arematics.minecraft.core.data.model.placeholder.DynamicPlaceholder;
import com.arematics.minecraft.core.data.service.chat.ChatThemeService;
import com.arematics.minecraft.core.data.service.chat.PlaceholderService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
public class PlaceholderController {

    public static final String PLACEHOLDER_DELIMITER = "%";
    private final Map<String, DynamicPlaceholder> placeholders = new HashMap<>();

    public void initPlaceholders() {
        ChatAPI.registerPlaceholder("rank");
        ChatAPI.registerPlaceholder("name");
        ChatAPI.registerPlaceholder("chatMessage");
        ChatAPI.registerPlaceholder("arematics");
    }

    public String convertToPlaceholder(String placeholder) {
        return PLACEHOLDER_DELIMITER + placeholder + PLACEHOLDER_DELIMITER;
    }

    public DynamicPlaceholder getPlaceholder(String placeholder) {
        return (placeholder.startsWith(PLACEHOLDER_DELIMITER) && placeholder.endsWith(PLACEHOLDER_DELIMITER))
                ? placeholders.get(placeholder) : placeholders.get(convertToPlaceholder(placeholder));
    }

    public void registerDynamicPlaceholder(String placeholder) {
        String placeholderFull = convertToPlaceholder(placeholder);
        DynamicPlaceholder dynamicPlaceholder = new DynamicPlaceholder();
        dynamicPlaceholder.setPlaceholderKey(placeholder);
        dynamicPlaceholder.setPlaceholderMatch(placeholderFull);
        PlaceholderService service = Boots.getBoot(CoreBoot.class).getContext().getBean(PlaceholderService.class);
        DynamicPlaceholder saved = service.save(dynamicPlaceholder);
        this.placeholders.put(placeholderFull, saved);
    }

}
