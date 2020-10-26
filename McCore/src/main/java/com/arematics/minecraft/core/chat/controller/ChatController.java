package com.arematics.minecraft.core.chat.controller;

import com.arematics.minecraft.core.chat.ChatAPI;
import com.arematics.minecraft.data.chat.placeholder.GlobalPlaceholderActions;
import com.arematics.minecraft.data.chat.placeholder.ThemePlaceholder;
import com.arematics.minecraft.data.chat.theme.ChatTheme;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageAction;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageReplace;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Getter
@Setter
@NoArgsConstructor
public class ChatController {

    private String chatMessage;

    public void registerSupplier(Player player) {
        ChatAPI.supplyPlaceholders("chatMessage", player, () -> chatMessage);
    }

    public void chat(Player player, String message) {
        setChatMessage(message);
        ChatAPI.getChatThemeController().getThemes().values().forEach(chatTheme -> {
            if (chatTheme.getActiveUsers().size() < 1) {
                return;
            }
            AdvancedMessageReplace advancedMessageReplace = this.createMessage(chatTheme);
            chatTheme.getThemePlaceholders().forEach(themePlaceholder -> {
                AdvancedMessageAction action = advancedMessageReplace.replace(themePlaceholder.getPlaceholderKey(), themePlaceholder.getValue());
                applyThemeActions(themePlaceholder, action);
                action.END();
            });
            chatTheme.getGlobalPlaceholderActions().forEach(globalPlaceholderActions -> {
                AdvancedMessageAction action = advancedMessageReplace.replace(globalPlaceholderActions.getPlaceholderKey(),
                        ChatAPI.getPlaceholder(globalPlaceholderActions.getPlaceholderKey()).getValues().get(player).get());
                applyGlobalActions(globalPlaceholderActions, action);
                action.END();
            });
            advancedMessageReplace.handle();
        });
    }

    /**
     * Creates a message for a theme, sets all selected players as recipients and sets the advanced injector
     *
     * @param chatTheme to use
     * @return AdvancedMessageReplace
     */
    private AdvancedMessageReplace createMessage(ChatTheme chatTheme) {
        CommandSender[] sendTo = chatTheme.getActiveUsers().stream().map(chatThemeUser -> (CommandSender) Bukkit.getPlayer(chatThemeUser.getPlayerId())).toArray(CommandSender[]::new);
        return Messages.create(chatTheme.getFormat()).to(sendTo).setInjector(AdvancedMessageInjector.class).disableServerPrefix();
    }

    /**
     * unwraps and sets actions for a theme placeholder
     *
     * @param themePlaceholder to inject click&hover
     * @param action           injector
     */
    private void applyThemeActions(ThemePlaceholder themePlaceholder, AdvancedMessageAction action) {
        if (themePlaceholder.getHoverAction() != null) {
            action.setHover(themePlaceholder.getHoverAction().getAction(), injectPlaceholderKeys(themePlaceholder.getHoverAction().getValue(), themePlaceholder));
        }
        if (themePlaceholder.getClickAction() != null) {
            action.setClick(themePlaceholder.getClickAction().getAction(), injectPlaceholderKeys(themePlaceholder.getClickAction().getValue(), themePlaceholder));
        }
    }

    /**
     * get placeholder action values from theme and applies it
     *
     * @param globalPlaceholderActions to get actions from
     * @param action                   injector
     */
    private void applyGlobalActions(GlobalPlaceholderActions globalPlaceholderActions, AdvancedMessageAction action) {
        if (globalPlaceholderActions.getHoverAction() != null) {
            action.setHover(globalPlaceholderActions.getHoverAction().getAction(),
                    injectPlaceholderKeys(globalPlaceholderActions.getHoverAction().getValue(),
                            PlaceholderController.getPlaceholderMatch(globalPlaceholderActions.getPlaceholderKey())));
        }
        if (globalPlaceholderActions.getClickAction() != null) {
            action.setClick(globalPlaceholderActions.getClickAction().getAction(),
                    injectPlaceholderKeys(globalPlaceholderActions.getClickAction().getValue(),
                            PlaceholderController.getPlaceholderMatch(globalPlaceholderActions.getPlaceholderKey())));
        }
    }

    /**
     * allows for using %key% to actual key in hover and click messages
     *
     * @param actionMessage to replace key
     * @param value         to inject
     * @return replaced action value
     */
    private String injectPlaceholderKeys(String actionMessage, String value) {
        return actionMessage.replace(PlaceholderController.getPlaceholderMatch("placeholderMatch"), value);
    }

    /**
     * allows for using placeholderKey and placeholderName to replace in hover and click messages,
     * this supports more replacements but only works for themeplaceholders for now
     *
     * @param actionMessage    to replace key
     * @param themePlaceholder belonging placeholder
     * @return replaced action value
     */
    private String injectPlaceholderKeys(String actionMessage, ThemePlaceholder themePlaceholder) {
        return actionMessage.replace(PlaceholderController.getPlaceholderMatch("placeholderMatch"), themePlaceholder.getPlaceholderMatch()).
                replace(PlaceholderController.getPlaceholderMatch("placeholderName"), themePlaceholder.getPlaceholderKey());
    }
}
