package com.arematics.minecraft.core.chat;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.chat.controller.ChatController;
import com.arematics.minecraft.core.chat.controller.ChatThemeController;
import com.arematics.minecraft.core.chat.controller.PlaceholderController;
import com.arematics.minecraft.data.global.model.ChatTheme;
import com.arematics.minecraft.data.global.model.GlobalPlaceholder;
import com.arematics.minecraft.data.global.model.GlobalPlaceholderAction;
import com.arematics.minecraft.data.global.model.ThemePlaceholder;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.UserService;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

@Component
public class ChatAPI {

    @Getter
    private static ChatThemeController chatThemeController;
    @Getter
    private static PlaceholderController placeholderController;
    @Getter
    private static ChatController chatController;

    @Autowired
    public ChatAPI(ChatController chatController, PlaceholderController placeholderController, ChatThemeController chatThemeController) {
        ChatAPI.chatController = chatController;
        ChatAPI.placeholderController = placeholderController;
        ChatAPI.chatThemeController = chatThemeController;
    }

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
        } else {
            System.out.println("SYSTEM HAT THEMES LOAD");
        }
    }

    public static void login(Player player) {
        supply(player);
    }

    // CHAT
    public static void chat(Player player, String message) {
        getChatController().chat(player, message);
    }

    //THEME
    public static Collection<ChatTheme> getThemes() {
        return getChatThemeController().getThemes().values();
    }

    public static boolean setTheme(Player player, String themeKey) {
        return getChatThemeController().setTheme(player, themeKey);
    }

    public static ChatTheme createTheme(String themeKey, Set<GlobalPlaceholderAction> globalPlaceholderActions, Set<ThemePlaceholder> themePlaceholders, String format) {
        return getChatThemeController().createTheme(themeKey, globalPlaceholderActions, themePlaceholders, format);
    }

    public static ChatTheme getTheme(String themeKey) {
        return getChatThemeController().getTheme(themeKey);
    }

    // PLACEHOLDER
    public static void registerPlaceholder(GlobalPlaceholder placeholder) {
        getPlaceholderController().registerPlaceholder(placeholder);
    }

    public static GlobalPlaceholder getPlaceholder(String placeholder) {
        return getPlaceholderController().getPlaceholder(placeholder);
    }

    public static void supply(Player player) {
        UserService service = Boots.getBoot(CoreBoot.class).getContext().getBean(UserService.class);
        getChatController().registerSupplier(player);
        User user = service.getUserByUUID(player.getUniqueId());
        supplyPlaceholders("rank", player, () -> user.getDisplayRank() != null ? user.getDisplayRank().getName() : user.getRank().getName());
        supplyPlaceholders("name", player, player::getDisplayName);
        supplyPlaceholders("arematics", player, () -> "§0[§1Arem§9atics§0]§r");
    }

    public static void supplyPlaceholders(String placeholderName, Player player, Supplier<String> supplier) {
        getPlaceholder(placeholderName).getValues().put(player, supplier);
    }

}
