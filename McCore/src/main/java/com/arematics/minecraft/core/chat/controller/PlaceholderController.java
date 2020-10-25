package com.arematics.minecraft.core.chat.controller;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.chat.ChatAPI;
import com.arematics.minecraft.core.data.model.placeholder.GlobalPlaceholder;
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
    private final Map<String, GlobalPlaceholder> placeholders = new HashMap<>();

    public void initPlaceholders() {
        ChatAPI.registerPlaceholder("rank");
        ChatAPI.registerPlaceholder("name");
        ChatAPI.registerPlaceholder("chatMessage");
        ChatAPI.registerPlaceholder("arematics");
    }

    public String convertToPlaceholder(String placeholder) {
        return PLACEHOLDER_DELIMITER + placeholder + PLACEHOLDER_DELIMITER;
    }

    public GlobalPlaceholder getPlaceholder(String placeholder) {
        return (placeholder.startsWith(PLACEHOLDER_DELIMITER) && placeholder.endsWith(PLACEHOLDER_DELIMITER))
                ? placeholders.get(placeholder) : placeholders.get(convertToPlaceholder(placeholder));
    }

    public void registerDynamicPlaceholder(String placeholder) {
        String placeholderFull = convertToPlaceholder(placeholder);
        GlobalPlaceholder globalPlaceholder = new GlobalPlaceholder();
        globalPlaceholder.setPlaceholderKey(placeholder);
        globalPlaceholder.setPlaceholderMatch(placeholderFull);
        PlaceholderService service = Boots.getBoot(CoreBoot.class).getContext().getBean(PlaceholderService.class);
        GlobalPlaceholder saved = service.save(globalPlaceholder);
        this.placeholders.put(placeholderFull, saved);
    }

}
