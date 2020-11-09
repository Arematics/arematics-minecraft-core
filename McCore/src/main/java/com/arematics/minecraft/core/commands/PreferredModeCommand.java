package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.language.LanguageAPI;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.*;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.data.global.model.Configuration;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.UserService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class PreferredModeCommand extends CoreCommand {

    private final UserService service;

    @Autowired
    public PreferredModeCommand(UserService userService){
        super("preferred-mode");
        this.service = userService;
    }

    private Part asPart(CommandSender sender, String name){
        return new Part(name)
                .setHoverAction(HoverAction.SHOW_TEXT, LanguageAPI.prepareRawMessage(sender, "preferred_mode_set")
                        .replaceAll("%mode%", name))
                .setClickAction(ClickAction.RUN_COMMAND, "/preferred-mode " + name);
    }

    @Override
    public void onDefaultExecute(CommandSender sender) {
        MSG modes = MSGBuilder.join(',', asPart(sender, "cli"), asPart(sender, "ui"));
        Messages.create("cmd_not_valid")
                .to(sender)
                .setInjector(AdvancedMessageInjector.class)
                .replace("cmd_usage", modes)
                .handle();
    }

    @SubCommand("{mode}")
    public boolean setPreferredMode(Player player, String mode) {
        if(Arrays.stream(new String[]{"cli", "ui"}).noneMatch(m -> m.equalsIgnoreCase(mode))) onDefaultExecute(player);
        User user = service.getUserByUUID(player.getUniqueId());
        user.getConfigurations().put("command-mode", new Configuration(mode));
        service.update(user);
        return true;
    }
}
