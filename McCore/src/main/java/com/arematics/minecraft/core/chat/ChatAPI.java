package com.arematics.minecraft.core.chat;

import com.arematics.minecraft.core.chat.controller.ChatThemeController;
import com.arematics.minecraft.core.chat.controller.PlaceholderController;
import com.arematics.minecraft.core.chat.model.ChatThemeUser;
import com.arematics.minecraft.core.chat.model.Placeholder;
import lombok.Getter;
import org.bukkit.entity.Player;

public class ChatAPI {

    @Getter
    private static final ChatThemeController chatThemeController = new ChatThemeController();
    @Getter
    private static final PlaceholderController placeholderController = new PlaceholderController();

    public static ChatThemeUser registerThemeUser(Player player){
        return getChatThemeController().registerPlayer(player);
    }

    public static ChatThemeUser getThemeUser(Player player){
        return getChatThemeController().getUsers().get(player);
    }

    public static void registerPlaceholder(String placeholder, boolean isStatic) {
        getPlaceholderController().registerPlaceholder(placeholder, isStatic);
    }

    public static void updatePlaceholder(String placeHolder, String replaceTo, Player player) {
        getPlaceholderController().updatePlaceholder(placeHolder, replaceTo, player);
    }

    public static Placeholder getPlaceholder(String placeholder){
        return getPlaceholderController().getPlaceholder(placeholder);
    }
}
