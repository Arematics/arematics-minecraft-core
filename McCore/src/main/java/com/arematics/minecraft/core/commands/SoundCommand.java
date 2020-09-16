package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.*;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.utils.ListUtils;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.guieffect.qual.UI;

@PluginCommand(aliases = {"s"})
@Permission(permission = "sound")
public class SoundCommand extends CoreCommand {

     public SoundCommand(){
         super("sound");
     }

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
    @Permission(permission = "list")
    public boolean list(CommandSender sender){
        return listSelected(sender, "");
    }

    @SubCommand("list {startsWith}")
    @Permission(permission = "list")
    public boolean listSelected(CommandSender sender, String startsWith){
        Messages.create("listing")
                .to(sender)
                .setInjector(AdvancedMessageInjector.class)
                .replace("list_type", "Sound")
                .END()
                .eachReplace("list_value", ListUtils.getNamesStartsWith(Sound.class, startsWith))
                .setHover(HoverAction.SHOW_TEXT, "/sound %value%")
                .setClick(ClickAction.RUN_COMMAND, "/sound %value%")
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
    public boolean executeSound(Player player, Sound sound){
        player.playSound(player.getLocation(), sound, 1, 1);
        return true;
    }
}
