package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.chat.controller.ChatThemeController;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.messaging.advanced.MSG;
import com.arematics.minecraft.core.messaging.advanced.MSGBuilder;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.global.model.ChatTheme;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
//@Perm(permission = "chattheme", description = "Change and inspect themes")
public class ThemeCommand extends CoreCommand {

    private final ChatThemeController chatThemeController;

    @Autowired
    public ThemeCommand(ChatThemeController chatThemeController) {
        super("theme", "chattheme");
        this.chatThemeController = chatThemeController;
    }


    @SubCommand("list")
    public boolean list(CorePlayer player) {
        List<Part> parts = new ArrayList<>();
        chatThemeController.getThemes().values().forEach(theme -> parts.add(new Part(theme.getThemeKey())));
        MSG msg = MSGBuilder.join(parts, ',');
        player.info("listing").setInjector(AdvancedMessageInjector.class).replace("list_type", new Part("Theme")).replace("list_value", msg).handle();
        return true;
    }

    @SubCommand("switch {theme}")
    public boolean switchCmd(CorePlayer player, ChatTheme theme) {
        player.setTheme(theme);
        player.info("theme_switched").DEFAULT().replace("theme", theme.getThemeKey()).handle();
        return true;
    }

    @SubCommand("info {theme}")
    public boolean info(CorePlayer player, ChatTheme theme) {
        player.info("theme_info").DEFAULT().replace("theme", theme.getThemeKey()).replace("theme_format", theme.getFormat()).handle();
        return true;
    }
}
