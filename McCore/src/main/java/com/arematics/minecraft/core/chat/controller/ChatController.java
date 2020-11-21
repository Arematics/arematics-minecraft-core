package com.arematics.minecraft.core.chat.controller;

import com.arematics.minecraft.core.chat.ChatAPI;
import com.arematics.minecraft.core.messaging.advanced.MSG;
import com.arematics.minecraft.data.global.model.ChatTheme;
import com.arematics.minecraft.data.global.model.PlaceholderAction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Component
public class ChatController {

    private String chatMessage;

    public void registerSupplier(Player player) {
        ChatAPI.supplyPlaceholders("chatMessage", player, () -> chatMessage);
    }

    public void chat(Player player, String message) {
        setChatMessage(message);
        ChatAPI.getThemes().forEach(chatTheme -> {
            if (chatTheme.getActiveUsers().size() < 1) {
                return;
            }
            MSG msg = buildThemeMessage(chatTheme, player);
            msg.sendAll(chatTheme.getActiveUsers(), new ArrayList<>());
        });
    }

    /**
     * creates a map out of all themeplaceholders and globalplaceholderactions with placeholder as key
     * @param theme to get actions from
     * @return map of all actions with delimited placeholder as key
     */
    public Map<String, PlaceholderAction> getActionsMap(ChatTheme theme) {
        Map<String, PlaceholderAction> actions = new HashMap<>();
        theme.getGlobalPlaceholderActions().forEach(globalPlaceholderAction -> mergeActionsIntoMap(actions, globalPlaceholderAction));
        theme.getThemePlaceholders().forEach(themePlaceholder -> mergeActionsIntoMap(actions, themePlaceholder));
        return actions;
    }

    private void mergeActionsIntoMap(Map<String, PlaceholderAction> actions, PlaceholderAction placeholderAction) {
        actions.put(PlaceholderController.applyDelimiter(placeholderAction.getPlaceholderKey()), placeholderAction);
    }

    /**
     * creates chat message in a theme for a given player
     * @param theme to use
     * @param player who chatted
     * @return themed chatmessage for player
     */
    private MSG buildThemeMessage(ChatTheme theme, Player player) {
        MSG msg = new MSG(theme.getFormat());
        Map<String, PlaceholderAction> actions = getActionsMap(theme);
        msg.createThemeParts(actions, player);
        return msg;
    }
}
