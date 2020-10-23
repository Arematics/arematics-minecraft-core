package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Default;
import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.PluginCommand;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.pages.Page;
import com.arematics.minecraft.core.pages.Pageable;
import com.arematics.minecraft.core.pages.Pager;
import com.arematics.minecraft.core.utils.ListUtils;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

@Component
@PluginCommand(aliases = {"s"})
@Perm(permission = "sound", description = "Allow usage to full /sound Command")
public class SoundCommand extends CoreCommand {

    public SoundCommand(){
        super("sound");
    }

    @Default
    public boolean sendInfo(CommandSender sender){
        Messages.create("cmd_not_valid")
                .to(sender)
                .setInjector(AdvancedMessageInjector.class)
                .replace("cmd_usage", "\n/sound list\n/sound list <startsWith>\n/sound <Name>")
                .END()
                .handle();
        return true;
    }

    @SubCommand("list")
    @Perm(permission = "list", description = "Allow access to list all sounds starting with given parameter")
    public boolean listAll(CommandSender sender){
        return listStartsWith(sender, "");
    }

    @SubCommand("list {startsWith}")
    @Perm(permission = "list", description = "Allow access to list all sounds starting with given parameter")
    public boolean listStartsWith(CommandSender sender, String startsWith){
        String key = startsWith.equals("") ? "sound list" : "sound list " + startsWith;
        Pager pager = Pager.of(sender);
        Pageable pageable = pager.fetch(key);
        if(pageable == null)
            pageable = pager.create(key, ListUtils.getNamesStartsWith(Sound.class, startsWith));
        Page current = pageable.current();
        Messages.create("listing")
                .to(sender)
                .setInjector(AdvancedMessageInjector.class)
                .replace("list_type", "Sound")
                .END()
                .eachReplace("list_value", current.getContent().toArray(new String[0]))
                .setHover(HoverAction.SHOW_TEXT, "/sound %value%")
                .setClick(ClickAction.RUN_COMMAND, "/sound %value%")
                .END()
                .handle();
        Pager.sendDefaultPageMessage(sender, key);
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
