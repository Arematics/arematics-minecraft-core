package com.arematics.minecraft.core.chat.controller;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.chat.ChatAPI;
import com.arematics.minecraft.data.global.model.GlobalPlaceholder;
import com.arematics.minecraft.data.service.PlaceholderService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@Component
public class PlaceholderController {

    public static final String PLACEHOLDER_DELIMITER = "%";
    private final Map<String, GlobalPlaceholder> placeholders = new HashMap<>();
    private final List<String> reservedPlaceholderKeys = new ArrayList<>();

    public void initPlaceholders() {
        ChatAPI.registerPlaceholder(createPlaceholder("rank"));
        ChatAPI.registerPlaceholder(createPlaceholder("name"));
        ChatAPI.registerPlaceholder(createPlaceholder("chatMessage"));
        ChatAPI.registerPlaceholder(createPlaceholder("arematics"));
        getReservedPlaceholderKeys().add("placeholderName");
        getReservedPlaceholderKeys().add("placeholderMatch");
        getReservedPlaceholderKeys().add("value");
    }


    public static String applyDelimiter(String placeholderKey) {
        return PLACEHOLDER_DELIMITER + placeholderKey + PLACEHOLDER_DELIMITER;
    }

    public static String stripPlaceholderDelimiter(String placeholderMatch) {
        return placeholderMatch.replaceAll(PLACEHOLDER_DELIMITER, "");
    }

    public GlobalPlaceholder getPlaceholder(String placeholderKey) {
       return getPlaceholders().get(placeholderKey);
    }

    private void validatePlaceholderKey(String placeholderKey) {
        if (getPlaceholders().containsKey(placeholderKey) || getReservedPlaceholderKeys().contains(placeholderKey)) {
            throw new UnsupportedOperationException("Der Placeholder: " + placeholderKey + " ist bereits registriert!");
        } else {
            getReservedPlaceholderKeys().add(placeholderKey);
        }
    }

    /**
     * saves and registers placeholder
     * @param placeholder created by createPlaceholder
     */
    public void registerPlaceholder(GlobalPlaceholder placeholder) {
        validatePlaceholderKey(placeholder.getPlaceholderKey());
        PlaceholderService service = Boots.getBoot(CoreBoot.class).getContext().getBean(PlaceholderService.class);
        GlobalPlaceholder saved = service.save(placeholder);
        getPlaceholders().put(placeholder.getPlaceholderKey(), saved);
    }

    /**
     * creates placeholder object doesnt save or register
     * @param placeholderKey key
     * @return placeholder object
     */
    public GlobalPlaceholder createPlaceholder(String placeholderKey) {
        GlobalPlaceholder placeholder = new GlobalPlaceholder();
        placeholder.setPlaceholderKey(placeholderKey);
        placeholder.setPlaceholderMatch(applyDelimiter(placeholderKey));
        return placeholder;
    }

    /**
     * loads and inits placeholders
     * @return if database contains placeholders, otherwise load defaults
     */
    public boolean loadGlobalPlaceholders() {
        PlaceholderService service = Boots.getBoot(CoreBoot.class).getContext().getBean(PlaceholderService.class);
        List<GlobalPlaceholder> placeholders = service.loadGlobals();
        if (null == placeholders || placeholders.size() < 3) {
            return false;
        }
        placeholders.forEach(this::registerPlaceholder);
        return true;
    }

}
