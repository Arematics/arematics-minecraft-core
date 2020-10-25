package com.arematics.minecraft.core.chat.controller;

import com.arematics.minecraft.core.chat.ChatAPI;
import com.arematics.minecraft.core.data.model.message.ChatClickAction;
import com.arematics.minecraft.core.data.model.message.ChatHoverAction;
import com.arematics.minecraft.core.data.model.placeholder.GlobalPlaceholder;
import com.arematics.minecraft.core.data.model.placeholder.ThemePlaceholder;
import com.arematics.minecraft.core.data.model.theme.ChatTheme;
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
            AdvancedMessageReplace advancedMessageReplace = this.createMessage(chatTheme);

            chatTheme.getThemePlaceholders().forEach(themePlaceholder -> {
                AdvancedMessageAction action = advancedMessageReplace.replace(themePlaceholder.getPlaceholderKey(), themePlaceholder.getValue());
                applyThemeActions(themePlaceholder, action);
                action.END();
            });

            chatTheme.getDynamicPlaceholderKeys().stream().map(ChatAPI::getPlaceholder).forEach(placeholder -> {
                AdvancedMessageAction action = advancedMessageReplace.replace(placeholder.getPlaceholderKey(), chatTheme.getPlaceholderThemeValues().get(placeholder.getPlaceholderKey()).get(player).get());
                applyDynamicActions(placeholder, chatTheme, action);
                action.END();
            });
            advancedMessageReplace.handle();
        });
    }

    /**
     * Creates a message for a theme, sets all selected players as recipients and sets the advanced injector
     * @param chatTheme to use
     * @return AdvancedMessageReplace
     */
    private AdvancedMessageReplace createMessage(ChatTheme chatTheme) {
        CommandSender[] sendTo = chatTheme.getActiveUsers().stream().map(chatThemeUser -> (CommandSender) Bukkit.getPlayer(chatThemeUser.getPlayerId())).toArray(CommandSender[]::new);
        return Messages.create(chatTheme.getFormat()).to(sendTo).setInjector(AdvancedMessageInjector.class).disableServerPrefix();
    }

    /**
     * unwraps and sets actions for a theme placeholder
     * @param themePlaceholder to inject click&hover
     * @param action injector
     */
    private void applyThemeActions(ThemePlaceholder themePlaceholder, AdvancedMessageAction action) {
        if(themePlaceholder.getHoverAction() != null) {
            action.setHover(themePlaceholder.getHoverAction().getAction(), themePlaceholder.getHoverAction().getValue());
        }
        if(themePlaceholder.getClickAction() != null) {
            action.setClick(themePlaceholder.getClickAction().getAction(), themePlaceholder.getClickAction().getValue());
        }
    }

    /**
     * get placeholder action values from theme and applies it
     * @param globalPlaceholder to inject
     * @param theme to get actions from
     * @param action injector
     */
    private void applyDynamicActions(GlobalPlaceholder globalPlaceholder, ChatTheme theme, AdvancedMessageAction action) {
        ChatHoverAction chatHoverAction = theme.getDynamicHoverActions().get(globalPlaceholder.getPlaceholderKey());
        if(chatHoverAction != null) {
            action.setHover(chatHoverAction.getAction(), chatHoverAction.getValue());
        }
        ChatClickAction chatClickAction = theme.getDynamicClickActions().get(globalPlaceholder.getPlaceholderKey());
        if(chatClickAction != null) {
            action.setClick(chatClickAction.getAction(), chatClickAction.getValue());
        }
    }


}
