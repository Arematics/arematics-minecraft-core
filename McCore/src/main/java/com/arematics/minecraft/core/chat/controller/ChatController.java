package com.arematics.minecraft.core.chat.controller;

import com.arematics.minecraft.core.chat.ChatAPI;
import com.arematics.minecraft.core.data.model.message.ChatClickAction;
import com.arematics.minecraft.core.data.model.message.ChatHoverAction;
import com.arematics.minecraft.core.chat.model.Placeholder;
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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            List<Placeholder> placeholders = Stream.concat(chatTheme.getDynamicPlaceholders().values().stream(), chatTheme.getThemePlaceholders().stream())
                    .collect(Collectors.toList());
            this.inject(chatTheme, placeholders, advancedMessageReplace, player);
            advancedMessageReplace.handle();
        });
    }

    private AdvancedMessageReplace createMessage(ChatTheme chatTheme) {
        CommandSender[] sendTo = chatTheme.getActiveUsers().stream().map(chatThemeUser -> (CommandSender) Bukkit.getPlayer(chatThemeUser.getPlayerId())).toArray(CommandSender[]::new);
        return Messages.create(chatTheme.getFormat()).to(sendTo).setInjector(AdvancedMessageInjector.class).disableServerPrefix();
    }

    private void inject(ChatTheme chatTheme, List<Placeholder> placeholders, AdvancedMessageReplace injector, Player player) {
        placeholders.forEach(placeholder -> {
            AdvancedMessageAction action = injector.replace(placeholder.getPlaceholderKey(), placeholder.getValue(player));
            this.applyActions(chatTheme, placeholder, action, player);
        });
    }

    private void applyActions(ChatTheme chatTheme, Placeholder placeholder, AdvancedMessageAction action, Player player) {
        if (placeholder.getClickAction(chatTheme.getThemeKey()) != null) {
            ChatClickAction clickAction = placeholder.getClickAction(chatTheme.getThemeKey());
            if(clickAction != null){
                action.setClick(clickAction.getAction(), prepareActionValue(placeholder, clickAction.getValue(), player));
            }
        }
        if (placeholder.getHoverAction(chatTheme.getThemeKey()) != null) {
            ChatHoverAction hoverAction = placeholder.getHoverAction(chatTheme.getThemeKey());
            if(hoverAction != null) {
                action.setHover(hoverAction.getAction(), prepareActionValue(placeholder, hoverAction.getValue(), player));
            }
        }
        action.END();
    }

    private String prepareActionValue(Placeholder placeholder, String chatActionValue, Player player) {
        return chatActionValue.replace("%key%", placeholder.getPlaceholderKey()).replace("%value%", placeholder.getValue(player));
    }
}
