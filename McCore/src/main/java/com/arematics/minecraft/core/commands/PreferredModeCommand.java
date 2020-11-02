package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.core.messaging.advanced.Part;
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
    private final Part[] modes;

    @Autowired
    public PreferredModeCommand(UserService userService){
        super("preferred-mode");
        this.service = userService;
        this.modes = new Part[]{toPerferredModePart("cli"), toPerferredModePart("ui")};
    }

    private Part toPerferredModePart(String name){
        return new Part(name)
                .setHoverAction(HoverAction.SHOW_TEXT, "Change preferred-mode to " + name)
                .setClickAction(ClickAction.RUN_COMMAND, "/preferred-mode " + name);
    }

    @Override
    public void onDefaultExecute(CommandSender sender) {
        Messages.create("cmd_not_valid")
                .to(sender)
                .setInjector(AdvancedMessageInjector.class)
                .eachReplace("cmd_usage", modes)
                .handle();
    }

    @SubCommand("{mode}")
    public boolean setPreferredMode(Player player, String mode) {
        if(Arrays.stream(modes).noneMatch(m -> m.TEXT.equalsIgnoreCase(mode))) onDefaultExecute(player);
        User user = service.getUserByUUID(player.getUniqueId());
        user.getConfigurations().put("command-mode", new Configuration(mode));
        service.update(user);
        return true;
    }
}
