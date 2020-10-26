package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Default;
import com.arematics.minecraft.core.annotations.PluginCommand;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.chat.ChatAPI;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.data.model.theme.ChatTheme;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

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
    
    @SubCommand("placeholdervalues {theme}}")
    public boolean showValues(Player player, String placeholder, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        ChatAPI.getPlaceholder(placeholder).getValues().forEach((s, supplier) -> {
            player.sendMessage(placeholder + " value for " + s + " : " + supplier.get());
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
        apiTheme.getGlobalPlaceholderActions().forEach(globalPlaceholderActions -> {
            player.sendMessage("dynamic key: " + globalPlaceholderActions.getPlaceholderKey() + " value: " + ChatAPI.getPlaceholder(globalPlaceholderActions.getPlaceholderKey()).getValues().get(player).get());
        });
        return true;
    }

}
