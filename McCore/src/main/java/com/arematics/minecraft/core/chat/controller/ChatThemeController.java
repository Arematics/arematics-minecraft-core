package com.arematics.minecraft.core.chat.controller;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.chat.ChatAPI;
import com.arematics.minecraft.core.chat.util.FormatBuilder;
import com.arematics.minecraft.core.chat.util.GlobalActionsBuilder;
import com.arematics.minecraft.core.chat.util.ThemePlaceholderBuilder;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.data.global.model.ChatHoverAction;
import com.arematics.minecraft.data.global.model.ChatTheme;
import com.arematics.minecraft.data.global.model.GlobalPlaceholderAction;
import com.arematics.minecraft.data.global.model.ThemePlaceholder;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.ChatThemeService;
import com.arematics.minecraft.data.service.UserService;
import lombok.Getter;
import org.bukkit.entity.Player;
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
    private final ChatThemeService service;
    private final UserService userService;
    private ChatAPI chatAPI;

    @Autowired
    public ChatThemeController(ChatThemeService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    public boolean loadThemes() {
        List<ChatTheme> savedThemes = service.getAll();
        System.out.println(savedThemes.size());
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
        Set<GlobalPlaceholderAction> globalActionsDebug = GlobalActionsBuilder.create().
                add(GlobalPlaceholderAction.builder().placeholderKey("rank").
                        hoverAction(ChatHoverAction.builder().action(HoverAction.SHOW_TEXT).value("%placeholderMatch% to %value%").build()).
                        build()).
                add(GlobalPlaceholderAction.builder().placeholderKey("name").
                        hoverAction(ChatHoverAction.builder().action(HoverAction.SHOW_TEXT).value("%placeholderMatch% to %value%").build()).
                        build()).
                add(GlobalPlaceholderAction.builder().placeholderKey("chatMessage").
                        hoverAction(ChatHoverAction.builder().action(HoverAction.SHOW_TEXT).value("%placeholderMatch% to %value%").build()).
                        build()).build();

        Set<GlobalPlaceholderAction> globalActionsRainbow = GlobalActionsBuilder.create().
                add(GlobalPlaceholderAction.builder().placeholderKey("rank").
                        hoverAction(ChatHoverAction.builder().action(HoverAction.SHOW_TEXT).value("§e%value%").build()).
                        build()).
                add(GlobalPlaceholderAction.builder().placeholderKey("name").
                        hoverAction(ChatHoverAction.builder().action(HoverAction.SHOW_TEXT).value("§a%value%").build()).
                        build()).
                add(GlobalPlaceholderAction.builder().placeholderKey("chatMessage").
                        hoverAction(ChatHoverAction.builder().action(HoverAction.SHOW_TEXT).value("§c%value%").build()).
                        build()).build();

        Set<ThemePlaceholder> themePlaceholdersDefault = ThemePlaceholderBuilder.create().add(ThemePlaceholder.builder().placeholderMatch("%chatDelim%").placeholderKey("chatDelim").value("»").build()).build();
        Set<ThemePlaceholder> themePlaceholdersRainbow = ThemePlaceholderBuilder.create().add(ThemePlaceholder.builder().placeholderMatch("%chatDelim%").placeholderKey("chatDelim").value(":").build()).build();
        Set<ThemePlaceholder> themePlaceholdersDebug = ThemePlaceholderBuilder.create().
                add(ThemePlaceholder.builder().placeholderMatch("%chatDelim%").placeholderKey("chatDelim").value(":").build()).
                add(ThemePlaceholder.builder().placeholderMatch("%debug%").placeholderKey("debug").value("[DEBUG]").
                        hoverAction(ChatHoverAction.builder().action(HoverAction.SHOW_TEXT).value("name: %placeholderName% match: %placeholderMatch% value: %value%").build()).build()).build();

        String defaultFormat = FormatBuilder.create().add("§8§l[").add("§7§l%rank%").add("§8§l]").space().add("§7%name%").add("§8%chatDelim%").space().add("§f%chatMessage%").build();
        String debugFormat = FormatBuilder.create().add("§c%debug%").space().add("%rank%").space().add("%name%").add("%chatDelim%").space().add("%chatMessage%").build();
        String rainbowFormat = FormatBuilder.create().add("§d%rank%").space().add("§5%name%").add("§1%chatDelim%").space().add("§e%chatMessage%").build();
        ChatTheme defaultTheme = createTheme("default", globalActionsDefault, themePlaceholdersDefault, defaultFormat);
        ChatTheme debugTheme = createTheme("debug", globalActionsDebug, themePlaceholdersDebug, debugFormat);
        ChatTheme rainbowTheme = createTheme("rainbow", globalActionsRainbow, themePlaceholdersRainbow, rainbowFormat);
        registerTheme(defaultTheme.getThemeKey(), defaultTheme);
        registerTheme(debugTheme.getThemeKey(), debugTheme);
        registerTheme(rainbowTheme.getThemeKey(), rainbowTheme);
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
        ChatTheme newTheme = getTheme(theme);
        if (null == newTheme) {
            return false;
        }
        User user = userService.getUserByUUID(player.getUniqueId());
        ChatTheme old = getTheme(user.getActiveTheme().getThemeKey());
        old.getActiveUsers().remove(user);
        user.setActiveTheme(newTheme);
        newTheme.getActiveUsers().add(user);
        return true;
    }

}
