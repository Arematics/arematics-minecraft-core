package com.arematics.minecraft.core.chat.controller;

import com.arematics.minecraft.core.chat.ChatAPI;
import com.arematics.minecraft.core.chat.util.FormatBuilder;
import com.arematics.minecraft.core.chat.util.GlobalActionsBuilder;
import com.arematics.minecraft.core.chat.util.ThemePlaceholderBuilder;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.data.global.model.ChatTheme;
import com.arematics.minecraft.data.global.model.GlobalPlaceholderAction;
import com.arematics.minecraft.data.global.model.ThemePlaceholder;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.ChatThemeService;
import com.arematics.minecraft.data.service.UserService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Component
public class ChatThemeController {

    private final Map<String, ChatTheme> themes = new HashMap<>();
    private final ChatThemeService chatThemeService;
    private final UserService userService;
    private ChatAPI chatAPI;

    @Autowired
    public ChatThemeController(ChatThemeService chatThemeService, UserService userService) {
        this.chatThemeService = chatThemeService;
        this.userService = userService;
    }

    public boolean loadThemes() {
        List<ChatTheme> savedThemes = chatThemeService.getAll();
        if (savedThemes.size() < 1) {
            return false;
        }
        savedThemes.forEach(theme -> registerTheme(theme.getThemeKey(), theme));
        return true;
    }

    /**
     *
     * sets up default themes and saves them to database, doesnt register them in system
     */
    public void createAndSaveDefaults() {
        Set<GlobalPlaceholderAction> globalActionsDefault = GlobalActionsBuilder.create().
                add(GlobalPlaceholderAction.builder().placeholderKey("rank").build()).
                add(GlobalPlaceholderAction.builder().placeholderKey("name").build()).
                add(GlobalPlaceholderAction.builder().placeholderKey("chatMessage").build()).build();

        Set<ThemePlaceholder> themePlaceholdersDefault = ThemePlaceholderBuilder.create().add(ThemePlaceholder.builder().placeholderMatch("%chatDelim%").placeholderKey("chatDelim").value("»").build()).build();

        String defaultFormat = FormatBuilder.create().add("§8§l[").add("%rank%").add("§8§l]").space().add("§7%name%").add("§8%chatDelim%").space().add("§f%chatMessage%").build();
        ChatTheme defaultTheme = createTheme("default", globalActionsDefault, themePlaceholdersDefault, defaultFormat);
        registerTheme(defaultTheme.getThemeKey(), defaultTheme);
    }

    /**
     * @param themeKey                  internal name of theme
     * @param dynamicPlaceholderActions create these through api to assign hover/click actions, contains placeholder key and actions
     * @param themePlaceholders         list of theme internal placeholders
     * @param format                    % encoded raw chat format
     * @return
     */
    public ChatTheme createTheme(String themeKey, Set<GlobalPlaceholderAction> dynamicPlaceholderActions, Set<ThemePlaceholder> themePlaceholders, String format) {
        ChatTheme chatTheme = new ChatTheme();
        chatTheme.setThemeKey(themeKey);
        chatTheme.setThemePlaceholders(themePlaceholders);
        chatTheme.setFormat(format);
        chatTheme.setGlobalPlaceholderActions(dynamicPlaceholderActions);
        return chatThemeService.save(chatTheme);
    }

    public ChatTheme getTheme(String name) {
        return getThemes().get(name);
    }

    public void registerTheme(String name, ChatTheme theme) {
        getThemes().put(name, theme);
    }


    /**
     * sets active theme for chatthemeuser and adds to senders chattheme
     *
     * @param player who is affected
     * @param theme  which is used
     */
    public boolean setTheme(CorePlayer player, String theme) {
        ChatTheme newTheme = getTheme(theme);
        if (null == newTheme) {
            return false;
        }
        User user = userService.getUserByUUID(player.getUUID());
        ChatTheme old = getTheme(user.getActiveTheme().getThemeKey());
        old.getActiveUsers().remove(user);
        user.setActiveTheme(newTheme);
        newTheme.getActiveUsers().add(user);
        return true;
    }

}
