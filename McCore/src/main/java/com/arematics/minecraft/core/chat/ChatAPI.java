package com.arematics.minecraft.core.chat;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.chat.controller.ChatController;
import com.arematics.minecraft.core.chat.controller.ChatThemeController;
import com.arematics.minecraft.core.chat.controller.PlaceholderController;
import com.arematics.minecraft.data.chat.placeholder.GlobalPlaceholder;
import com.arematics.minecraft.data.chat.placeholder.GlobalPlaceholderActions;
import com.arematics.minecraft.data.chat.placeholder.ThemePlaceholder;
import com.arematics.minecraft.data.chat.theme.ChatTheme;
import com.arematics.minecraft.data.chat.theme.ChatThemeUser;
import com.arematics.minecraft.data.service.UserService;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Supplier;

@Component
public class ChatAPI {

    @Getter
    private static final ChatThemeController chatThemeController = new ChatThemeController();
    @Getter
    private static final PlaceholderController placeholderController = new PlaceholderController();
    @Getter
    private static final ChatController chatController = new ChatController();

    // bootstrap shit
    public static void bootstrap() {
        if (!getPlaceholderController().loadGlobalPlaceholders()) {
            System.out.println("SYSTEM HAT PLACEHOLDER INIT");
            getPlaceholderController().initPlaceholders();
        } else {
            System.out.println("SYSTEM HAT PLACEHOLDER LOAD");
        }
        if (!getChatThemeController().loadThemes()) {
            System.out.println("SYSTEM HAT THEMES INIT");
            getChatThemeController().createAndSaveDefaults();
            getChatThemeController().loadThemes();
        } else {
            System.out.println("SYSTEM HAT THEMES LOAD");
        }
    }

    public static void login(Player player) {
        getChatThemeController().register(player);
        supply(player);
    }

    public static void logout(Player player) {
        getChatThemeController().logout(player);
    }

    public static void supply(Player player) {
        UserService service = Boots.getBoot(CoreBoot.class).getContext().getBean(UserService.class);
        getChatController().registerSupplier(player);
        supplyPlaceholders("rank", player, () -> service.getUserByUUID(player.getUniqueId()).getDisplayRank().getName());
        supplyPlaceholders("name", player, player::getDisplayName);
        supplyPlaceholders("arematics", player, () -> "§0[§1Arem§9atics§0]§r");
    }

    public static void supplyPlaceholders(String placeholderName, Player player, Supplier<String> supplier) {
        GlobalPlaceholder placeholder = getPlaceholder(placeholderName);
        Bukkit.broadcastMessage(placeholder.toString());
        Map<Player, Supplier<String>> placeholderValues = placeholder.getValues();
        placeholderValues.put(player, supplier);
    }

    // CHAT
    public static void chat(Player player, String message) {
        getChatController().chat(player, message);
    }

    // CHAT THEMES
    public static Map<UUID, ChatThemeUser> getUsers() {
        return getChatThemeController().getUsers();
    }

    public static ChatThemeUser getThemeUser(Player player) {
        return getUsers().get(player.getUniqueId());
    }

    public static boolean setTheme(Player player, String themeKey) {
        return getChatThemeController().setTheme(player, themeKey);
    }

    public static ChatTheme createTheme(String themeKey, List<GlobalPlaceholderActions> dynamicPlaceholderNames, Set<ThemePlaceholder> themePlaceholders, String format) {
        return getChatThemeController().createTheme(themeKey, dynamicPlaceholderNames, themePlaceholders, format);
    }

    public static ChatTheme getTheme(String themeKey) {
        return getChatThemeController().getTheme(themeKey);
    }

    public static void registerTheme(String name, ChatTheme theme) {
        getChatThemeController().registerTheme(name, theme);
    }

    // PLACEHOLDER
    public static void registerPlaceholder(GlobalPlaceholder placeholder) {
        getPlaceholderController().registerPlaceholder(placeholder);
    }

    public static GlobalPlaceholder createPlaceholder(String placeholderKey) {
        return getPlaceholderController().createPlaceholder(placeholderKey);
    }


    public static GlobalPlaceholder getPlaceholder(String placeholder) {
        return getPlaceholderController().getPlaceholder(placeholder);
    }

}
