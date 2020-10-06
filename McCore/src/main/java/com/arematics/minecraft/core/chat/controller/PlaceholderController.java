package com.arematics.minecraft.core.chat.controller;

import com.arematics.minecraft.core.chat.ChatAPI;
import com.arematics.minecraft.core.data.model.placeholder.DynamicPlaceholder;
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
        this.placeholders.put(placeholderFull, dynamicPlaceholder);
    }

}
