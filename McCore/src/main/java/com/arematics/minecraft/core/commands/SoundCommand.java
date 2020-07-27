package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.*;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.utils.ListUtils;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@PluginCommand(names = {"sound"})
@Permission(permission = "sound")
public class SoundCommand extends CoreCommand {

    @Default
    public boolean sendInfo(CommandSender sender){
        Messages.create("cmd_not_valid")
                .to(sender)
                .DEFAULT()
                .replace("cmd_usage", "\n/sound list\n/sound list <startsWith>\n/sound <Name>")
                .handle();
        return true;
    }

    @SubCommand("list")
    public boolean list(Player player){
        return listSelected(player, "");
    }

    @SubCommand("list {startsWith}")
    @Permission(permission = "list")
    public boolean listSelected(CommandSender sender, String startsWith){
        Messages.create("listing")
                .to(sender)
                .setInjector(AdvancedMessageInjector.class)
                .replace("list_type", "Sound")
                .setHover(HoverAction.SHOW_TEXT, "Test")
                .END()
                .replace("list_value", ListUtils.getNameListStartsWith(Sound.class, startsWith))
                .END()
                .handle();
        return true;
    }

    @SubCommand("list add {message}")
    public boolean addSelected(CommandSender sender, String message){
        sender.sendMessage(message);
        return true;
    }

    @SubCommand("{sound}")
    public boolean executeSound(CommandSender sender, Sound sound){
        Player player = (Player) sender;
        player.playSound(player.getLocation(), sound, 1, 1);
        return true;
    }
}
