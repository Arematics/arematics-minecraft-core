package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
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
    private final String[] modes;

    @Autowired
    public PreferredModeCommand(UserService userService){
        super("preferred-mode");
        this.service = userService;
        this.modes = new String[]{"cli", "ui"};
    }

    @Override
    public boolean onDefaultExecute(CommandSender sender) {
        Messages.create("cmd_not_valid")
                .to(sender)
                .setInjector(AdvancedMessageInjector.class)
                .eachReplace("cmd_usage", modes)
                .setHover(HoverAction.SHOW_TEXT, "Change preferred-mode to %value")
                .setClick(ClickAction.RUN_COMMAND, "/preferred-mode %value%")
                .END()
                .handle();
        return true;
    }

    @SubCommand("{mode}")
    public boolean setPreferredMode(Player player, String mode) {
        if(Arrays.stream(modes).noneMatch(m -> m.equalsIgnoreCase(mode))) onDefaultExecute(player);
        User user = service.getUserByUUID(player.getUniqueId());
        user.getConfigurations().put("command-mode", new Configuration(mode));
        service.update(user);
        return true;
    }
}
