package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.language.LanguageAPI;
import com.arematics.minecraft.core.utils.ListUtils;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@com.arematics.minecraft.core.annotations.Command
public class SoundCommand extends CoreCommand {

    private static final String NOT_VALID_LENGHT = "cmd_not_valid_length";

    @Override
    public String[] getCommandNames() {
        return new String[]{"sound"};
    }

    @Override
    public boolean matchAnyAccess() {
        return true;
    }

    @Override
    public boolean validate(CommandSender sender, Command command, String label, String[] subs, int length) {
        if(length != 1){
            LanguageAPI.sendWarning((Player)sender, NOT_VALID_LENGHT);
            return false;
        }

        String sub = subs[0];
        if(sub.equals("list"))
            LanguageAPI.injectable("listing")
            .inject("%list_type%", () -> "Sounds")
            .inject("%list_value%", () -> ListUtils.getNameList(Sound.class))
            .send((Player)sender);
        else{
            Player player = (Player) sender;
            player.playSound(player.getLocation(), Sound.valueOf(sub), 1, 1);
        }
        return true;
    }
}
