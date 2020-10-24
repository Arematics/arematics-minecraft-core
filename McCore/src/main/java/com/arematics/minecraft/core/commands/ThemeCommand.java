package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.annotations.Default;
import com.arematics.minecraft.core.annotations.PluginCommand;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.chat.ChatAPI;
import com.arematics.minecraft.core.chat.model.PlaceholderActionInput;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.data.model.message.ChatClickAction;
import com.arematics.minecraft.core.data.model.message.ChatHoverAction;
import com.arematics.minecraft.core.data.model.placeholder.ThemePlaceholder;
import com.arematics.minecraft.core.data.model.theme.ChatTheme;
import com.arematics.minecraft.core.data.service.chat.ChatThemeService;
import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@PluginCommand(aliases = {"chattheme"})
@Component
public class ThemeCommand extends CoreCommand {

    public ThemeCommand() {
        super("theme");
    }

    @Default
    public boolean help(Player player) {
        player.sendMessage("/theme list");
        player.sendMessage("/theme [theme]");
        player.sendMessage(ChatAPI.getThemeUser(player).getActiveTheme().getThemeKey());
        return true;
    }

    @SubCommand("list")
    public boolean listThemes(Player player) {
        ChatAPI.getChatThemeController().getThemes().values().forEach(theme -> {
            player.sendMessage(theme.getThemeKey());
        });
        return true;
    }
    
    @SubCommand("placeholdervalues {theme}")
    public boolean listPlaceholderValues(Player player, String theme) {
        ChatAPI.getTheme(theme).getPlaceholderThemeValues().forEach((s, playerSupplierMap) -> {
            Bukkit.broadcastMessage("value for placeholder: " + s + " " + playerSupplierMap.get(player).get());
        });
        return true;
    }

    @SubCommand("{theme}")
    public boolean switchTheme(Player player, String theme) {
        if(ChatAPI.setTheme(player, theme)){
            player.sendMessage("theme gewechselt zu " + theme);
        } else {
            player.sendMessage("theme not found");
        }
        return true;
    }

    @SubCommand("info {theme}")
    public boolean info(Player player, String theme) {
        ChatTheme apiTheme = Boots.getBoot(CoreBoot.class).getContext().getBean(ChatThemeService.class).get(theme);
        Bukkit.broadcastMessage(apiTheme.toString());
        return true;
    }

    @SubCommand("new {theme}")
    public boolean create(Player player, String theme) {
        ChatAPI.registerPlaceholder("api");
        List<PlaceholderActionInput> dynamicAndActions = new ArrayList<PlaceholderActionInput>() {{
            add(new PlaceholderActionInput("rank", null, null));
            add(new PlaceholderActionInput("name", null, null));
            add(new PlaceholderActionInput("chatMessage", null, null));
            add(new PlaceholderActionInput("arematics", new ChatHoverAction(HoverAction.SHOW_TEXT, "Unser Discord"), new ChatClickAction(ClickAction.OPEN_URL, "https://discordapp.com/invite/AAXk9Jb")));
            add(new PlaceholderActionInput("api", new ChatHoverAction(HoverAction.SHOW_TEXT, "API"), new ChatClickAction(ClickAction.OPEN_URL, "https://api.com")));
        }};
        Set<ThemePlaceholder> themePlaceholders = new HashSet<ThemePlaceholder>() {{
            ThemePlaceholder chatDelim = new ThemePlaceholder();
            chatDelim.setBelongingThemeKey("default");
            chatDelim.setPlaceholderMatch("%chatDelim%");
            chatDelim.setPlaceholderKey("chatDelim");
            chatDelim.setValue(":");
            add(chatDelim);
        }};
        ChatTheme newTheme = ChatAPI.createTheme(theme, dynamicAndActions, themePlaceholders, "%api% %arematics% %rank% %name%%chatDelim% %chatMessage%");
        ChatThemeService service = Boots.getBoot(CoreBoot.class).getContext().getBean(ChatThemeService.class);
        ChatTheme saved = service.get(newTheme.getThemeKey());
        ChatAPI.registerTheme(saved.getThemeKey(), saved);
        ChatAPI.getUsers().values().forEach(user -> {
            ChatAPI.supply(Bukkit.getPlayer(user.getPlayerId()));
        });
        return true;
    }

    @SubCommand("inspect {theme}")
    public boolean inspect(Player player, String theme) {
        ChatTheme apiTheme = ChatAPI.getTheme(theme);
        player.sendMessage(apiTheme.getThemeKey());
        player.sendMessage(apiTheme.getFormat());
        apiTheme.getActiveUsers().forEach(user -> {
            player.sendMessage(user.getPlayerId().toString());
        });
        apiTheme.getThemePlaceholders().forEach(themePlaceholder -> {
            player.sendMessage("themeplaceholderkey: " + themePlaceholder.getPlaceholderKey() + " value: " + themePlaceholder.getValue());
        });
        return true;
    }

}
