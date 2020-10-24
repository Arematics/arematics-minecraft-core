package com.arematics.minecraft.core.chat.model;

import com.arematics.minecraft.core.data.model.message.ChatClickAction;
import com.arematics.minecraft.core.data.model.message.ChatHoverAction;
import com.arematics.minecraft.core.data.model.theme.ChatTheme;
import org.bukkit.entity.Player;

import java.util.Map;

public interface Placeholder {
    String getPlaceholderKey();
    String getPlaceholderMatch();
    boolean isStatic();
    String getValue(ChatTheme chatTheme, Player player);
    ChatClickAction getClickAction(String themeKey);
    ChatHoverAction getHoverAction(String themeKey);
    void setClickAction(String themeKey, ChatClickAction clickAction);
    void setHoverAction(String themeKey, ChatHoverAction hoverAction);
}
