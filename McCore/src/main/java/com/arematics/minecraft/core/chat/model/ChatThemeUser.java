package com.arematics.minecraft.core.chat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
@Setter
@NoArgsConstructor
public class ChatThemeUser {

    private Player player;
    private ChatTheme activeTheme = new ChatTheme();
}
