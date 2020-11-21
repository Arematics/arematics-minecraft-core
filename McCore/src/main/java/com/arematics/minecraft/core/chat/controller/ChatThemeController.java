package com.arematics.minecraft.core.chat.controller;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.chat.ChatAPI;
import com.arematics.minecraft.data.global.model.GlobalPlaceholderAction;
import com.arematics.minecraft.data.global.model.ChatClickAction;
import com.arematics.minecraft.data.global.model.ChatHoverAction;
import com.arematics.minecraft.data.global.model.ThemePlaceholder;
import com.arematics.minecraft.data.global.model.ChatTheme;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.ChatThemeService;
import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.data.service.UserService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@NoArgsConstructor
@Component
public class ChatThemeController {

    private final Map<String, ChatTheme> themes = new HashMap<>();

    public boolean loadThemes() {
        ChatThemeService service = Boots.getBoot(CoreBoot.class).getContext().getBean(ChatThemeService.class);
        List<ChatTheme> savedThemes = service.getAll();
        System.out.println(savedThemes.size());
        if (savedThemes.size() < 1) {
            return false;
        }
        savedThemes.forEach(theme -> registerTheme(theme.getThemeKey(), theme));
        return true;
    }

    /**
     * sets up default themes and saves them to database, doesnt register them in system
     */
    public void createAndSaveDefaults() {
        Set<GlobalPlaceholderAction> defaultDynamicAndActions = new HashSet<GlobalPlaceholderAction>() {{
            GlobalPlaceholderAction rank = GlobalPlaceholderAction.builder().placeholderKey("rank").build();
            GlobalPlaceholderAction name = GlobalPlaceholderAction.builder().placeholderKey("name").build();
            GlobalPlaceholderAction chatMessage = GlobalPlaceholderAction.builder().placeholderKey("chatMessage").build();
            GlobalPlaceholderAction arematics = GlobalPlaceholderAction.builder().placeholderKey("arematics").
                    hoverAction(ChatHoverAction.builder().action(HoverAction.SHOW_TEXT).value("Join unserem Discord!").build()).
                    clickAction(ChatClickAction.builder().action(ClickAction.OPEN_URL).value("https://discordapp.com/invite/AAXk9Jb").build()).
                    build();
            add(rank);
            add(name);
            add(chatMessage);
            add(arematics);
        }};

        Set<GlobalPlaceholderAction> debugDynamicAndActions = new HashSet<GlobalPlaceholderAction>() {{
            GlobalPlaceholderAction rank = GlobalPlaceholderAction.builder().placeholderKey("rank").
                    hoverAction(ChatHoverAction.builder().action(HoverAction.SHOW_TEXT).value("%placeholderMatch% to %value%").build()).
                    build();
            GlobalPlaceholderAction name = GlobalPlaceholderAction.builder().placeholderKey("name").
                    hoverAction(ChatHoverAction.builder().action(HoverAction.SHOW_TEXT).value("%placeholderMatch% to %value%").build()).
                    build();
            GlobalPlaceholderAction chatMessage = GlobalPlaceholderAction.builder().placeholderKey("chatMessage").
                    hoverAction(ChatHoverAction.builder().action(HoverAction.SHOW_TEXT).value("%placeholderMatch% to %value%").build()).
                    build();
            add(rank);
            add(name);
            add(chatMessage);
        }};

        Set<ThemePlaceholder> themePlaceholders = new HashSet<ThemePlaceholder>() {{
            ThemePlaceholder chatDelim = ThemePlaceholder.builder().placeholderMatch("%chatDelim%").placeholderKey("chatDelim").value("|").build();
            add(chatDelim);
        }};
        Set<ThemePlaceholder> themePlaceholdersDebug = new HashSet<ThemePlaceholder>() {{
            ThemePlaceholder chatDelim = ThemePlaceholder.builder().placeholderMatch("%chatDelim%").placeholderKey("chatDelim").value(":").build();
            ThemePlaceholder debug = ThemePlaceholder.builder().placeholderMatch("%debug%").placeholderKey("debug").value("[DEBUG]").
                    hoverAction(ChatHoverAction.builder().action(HoverAction.SHOW_TEXT).value("name: %placeholderName% match: %placeholderMatch% value: %value%").build()).build();
            add(debug);
            add(chatDelim);
        }};
        ChatTheme defaultTheme = createTheme("default", defaultDynamicAndActions, themePlaceholders, "%arematics%, ,%rank%, ,%name%,%chatDelim%, ,%chatMessage%,");
        ChatTheme debugTheme = createTheme("debug", debugDynamicAndActions, themePlaceholdersDebug, "%debug%, ,%rank%, ,%name%,%chatDelim%, ,%chatMessage%,");
        registerTheme(defaultTheme.getThemeKey(), defaultTheme);
        registerTheme(debugTheme.getThemeKey(), debugTheme);
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
        ChatThemeService service = Boots.getBoot(CoreBoot.class).getContext().getBean(ChatThemeService.class);
        return service.save(chatTheme);
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
    public boolean setTheme(Player player, String theme) {
        ChatTheme newTheme = ChatAPI.getTheme(theme);
        if (null == newTheme) {
            return false;
        }
        UserService service = Boots.getBoot(CoreBoot.class).getContext().getBean(UserService.class);
        User user = service.getUserByUUID(player.getUniqueId());
        ChatTheme old = ChatAPI.getTheme(user.getActiveTheme().getThemeKey());
        old.getActiveUsers().remove(user);
        user.setActiveTheme(newTheme);
        newTheme.getActiveUsers().add(user);
        return true;
    }

}
