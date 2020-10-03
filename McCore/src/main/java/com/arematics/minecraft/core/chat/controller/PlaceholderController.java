package com.arematics.minecraft.core.chat.controller;

import com.arematics.minecraft.core.chat.model.DynamicPlaceholder;
import com.arematics.minecraft.core.chat.model.Placeholder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
public class PlaceholderController {

    public static final String PLACEHOLDER_DELIMITER = "%";
    private final Map<String, Placeholder> placeholders = new HashMap<>();

    public String convertToPlaceholder(String placeholder) {
        return PLACEHOLDER_DELIMITER + placeholder + PLACEHOLDER_DELIMITER;
    }


    public void updatePlaceholder(String placeholderName, String placeholderValue, Player player) {
        Placeholder toUpdate = getPlaceholder(placeholderName);
        toUpdate.getPlaceholderValues().put(player, placeholderValue);
    }

    public Placeholder getPlaceholder(String placeholder) {
        return (placeholder.startsWith(PLACEHOLDER_DELIMITER) && placeholder.endsWith(PLACEHOLDER_DELIMITER))
        ? placeholders.get(placeholder) : placeholders.get(convertToPlaceholder(placeholder));
    }

    public void registerPlaceholder(String placeholder, boolean isStatic) {
        if (isStatic) {

        } else {
            registerDynamicPlaceholder(placeholder);
        }
    }

    private void registerDynamicPlaceholder(String placeholder) {
        String placeholderFull = convertToPlaceholder(placeholder);
        DynamicPlaceholder dynamicPlaceholder = new DynamicPlaceholder();
        dynamicPlaceholder.setPlaceholderName(placeholder);
        dynamicPlaceholder.setPlaceholderMatch(placeholderFull);
        this.placeholders.put(placeholderFull, dynamicPlaceholder);
    }

}
