package com.arematics.minecraft.core.chat.controller;

import com.arematics.minecraft.core.chat.model.ChatThemeUser;
import com.arematics.minecraft.core.chat.model.ChatTheme;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ChatThemeController {

    private final Map<Player, ChatThemeUser> users = new HashMap<>();
    private final Map<ChatThemeUser, ChatTheme[]> availableThemes = new HashMap<>();
    private final Map<String, ChatTheme> themes = new HashMap<>();

    public ChatThemeUser registerPlayer(Player player) {
        ChatThemeUser chatThemeUser = new ChatThemeUser();
        chatThemeUser.setActiveTheme(new ChatTheme());
        chatThemeUser.setPlayer(player);
        this.users.put(player, chatThemeUser);
        return chatThemeUser;
    }

}
