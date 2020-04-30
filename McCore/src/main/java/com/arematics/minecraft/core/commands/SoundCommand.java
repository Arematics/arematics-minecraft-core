package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.command.*;
import com.arematics.minecraft.core.language.LanguageAPI;
import com.arematics.minecraft.core.utils.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.util.Date;

@CMD
public class SoundCommand extends CoreCommand {

    @Override
    public String[] getCommandNames() {
        return new String[]{"sound"};
    }

    @Override
    public boolean matchAnyAccess() {
        return true;
    }

    @Default
    public boolean sendInfo(CommandSender sender){
        LanguageAPI.injectable("cmd_not_valid")
                .inject("%cmd_usage%", () -> "\n/sound list\n/sound list <startsWith>\n/sound <Name>")
                .send((Player)sender);
        return true;
    }

    @Sub("list")
    public boolean list(CommandSender sender){
        return listSelected(sender, "");
    }

    @Sub("list {startsWith}")
    public boolean listSelected(CommandSender sender, String startsWith){
        LanguageAPI.injectable("listing")
                .inject("%list_type%", () -> "Sound")
                .inject("%list_value%", () -> ListUtils.getNameListStartsWith(Sound.class, startsWith))
                .send((Player)sender);
        return true;
    }

    @Sub("list date {date}")
    public boolean executeDate(CommandSender sender, Date date){
        sender.sendMessage(date.toString());
        return true;
    }

    @Sub("list add {message}")
    public boolean addSelected(CommandSender sender, String message){
        sender.sendMessage(message);
        return true;
    }

    @Sub("{sound}")
    public boolean executeSound(CommandSender sender, Sound sound){
        Player player = (Player) sender;
        player.playSound(player.getLocation(), sound, 1, 1);
        return true;
    }
}
