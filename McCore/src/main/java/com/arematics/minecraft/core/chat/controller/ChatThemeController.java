package com.arematics.minecraft.core.chat.controller;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.chat.ChatAPI;
import com.arematics.minecraft.data.global.model.GlobalPlaceholderActions;
import com.arematics.minecraft.data.global.model.ChatClickAction;
import com.arematics.minecraft.data.global.model.ChatHoverAction;
import com.arematics.minecraft.data.global.model.ThemePlaceholder;
import com.arematics.minecraft.data.global.model.ChatTheme;
import com.arematics.minecraft.data.global.model.ChatThemeUser;
import com.arematics.minecraft.data.service.ChatThemeService;
import com.arematics.minecraft.data.service.ChatThemeUserService;
import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

import java.util.*;

@Getter
@NoArgsConstructor
@Component
public class ChatThemeController {

    private final Map<UUID, ChatThemeUser> users = new HashMap<>();
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

    public GlobalPlaceholderActions buildActions(String name, ChatHoverAction hoverAction, ChatClickAction clickAction) {
        GlobalPlaceholderActions actions = new GlobalPlaceholderActions();
        actions.setPlaceholderKey(name);
        actions.setHoverAction(hoverAction);
        actions.setClickAction(clickAction);
        return actions;
    }

    /**
     * sets up default themes and saves them to database, doesnt register them in system
     */
    public void createAndSaveDefaults() {
        List<GlobalPlaceholderActions> defaultDynamicAndActions = new ArrayList<GlobalPlaceholderActions>() {{
            GlobalPlaceholderActions rank = buildActions("rank", null, null);
            GlobalPlaceholderActions name = buildActions("name", null, null);
            GlobalPlaceholderActions chatMessage = buildActions("chatMessage", null, null);
            GlobalPlaceholderActions arematics = buildActions("arematics", new ChatHoverAction(HoverAction.SHOW_TEXT, "Unser Discord"), new ChatClickAction(ClickAction.OPEN_URL, "https://discordapp.com/invite/AAXk9Jb"));
            add(rank);
            add(name);
            add(chatMessage);
            add(arematics);
        }};

        List<GlobalPlaceholderActions> debugDynamicAndActions = new ArrayList<GlobalPlaceholderActions>() {{
            GlobalPlaceholderActions rank = buildActions("rank",  new ChatHoverAction(HoverAction.SHOW_TEXT, "%placeholderMatch% to %value%"), null);
            GlobalPlaceholderActions name = buildActions("name",  new ChatHoverAction(HoverAction.SHOW_TEXT, "%placeholderMatch% to %value%"), null);
            GlobalPlaceholderActions chatMessage = buildActions("chatMessage",  new ChatHoverAction(HoverAction.SHOW_TEXT, "%placeholderMatch% to %value%"), null);
            add(rank);
            add(name);
            add(chatMessage);
        }};

        Set<ThemePlaceholder> themePlaceholders = new HashSet<ThemePlaceholder>() {{
            ThemePlaceholder chatDelim = new ThemePlaceholder();
            chatDelim.setPlaceholderMatch("%chatDelim%");
            chatDelim.setPlaceholderKey("chatDelim");
            chatDelim.setValue(":");
            add(chatDelim);
        }};
        Set<ThemePlaceholder> themePlaceholdersDebug = new HashSet<ThemePlaceholder>() {{
            ThemePlaceholder chatDelim = new ThemePlaceholder();
            chatDelim.setPlaceholderMatch("%chatDelim%");
            chatDelim.setPlaceholderKey("chatDelim");
            chatDelim.setValue(":");
            ThemePlaceholder debug = new ThemePlaceholder();
            debug.setPlaceholderMatch("%debug%");
            debug.setPlaceholderKey("debug");
            debug.setValue("[Debug]");
            debug.setHoverAction(new ChatHoverAction(HoverAction.SHOW_TEXT, "name: %placeholderName% match: %placeholderMatch%"));
            add(debug);
            add(chatDelim);
        }};
        ChatTheme defaultTheme = ChatAPI.createTheme("default", defaultDynamicAndActions, themePlaceholders, "%arematics% %rank% %name%%chatDelim% %chatMessage%");
        ChatTheme debugTheme = ChatAPI.createTheme("debug", debugDynamicAndActions, themePlaceholdersDebug, "%debug% %rank% %name%%chatDelim% %chatMessage%");
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
    public ChatTheme createTheme(String themeKey, List<GlobalPlaceholderActions> dynamicPlaceholderActions, Set<ThemePlaceholder> themePlaceholders, String format) {
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
     * registers player as chattheme user, is called on player join
     *
     * @param player to register
     * @return
     */
    public ChatThemeUser register(Player player) {
        ChatThemeUser user = Boots.getBoot(CoreBoot.class).getContext().getBean(ChatThemeUserService.class).getOrCreate(player.getUniqueId());
        ChatTheme theme = ChatAPI.getTheme(user.getActiveTheme().getThemeKey());
        theme.getActiveUsers().add(user);
        getUsers().put(player.getUniqueId(), user);
        return user;
    }

    public void logout(Player player) {
        ChatThemeUserService service = Boots.getBoot(CoreBoot.class).getContext().getBean(ChatThemeUserService.class);
        ChatThemeUser user = getUser(player);
        service.save(user);
        ChatAPI.getTheme(user.getActiveTheme().getThemeKey()).getActiveUsers().remove(user);
        getUsers().remove(player.getUniqueId());
    }

    public ChatThemeUser getUser(Player player) {
        return getUsers().get(player.getUniqueId());
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
        ChatThemeUser user = getUser(player);
        ChatTheme old = ChatAPI.getTheme(user.getActiveTheme().getThemeKey());
        old.getActiveUsers().remove(user);
        user.setActiveTheme(newTheme);
        newTheme.getActiveUsers().add(user);
        return true;
    }

}
