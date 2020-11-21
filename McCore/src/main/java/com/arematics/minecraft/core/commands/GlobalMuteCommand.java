package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

@Component
public class GlobalMuteCommand extends CoreCommand {

    public static boolean isGlobalMuteActive = false;

    public GlobalMuteCommand() {
        super("globalmute", "glm");
    }

    @Override
    public void onDefaultExecute(CommandSender sender) {

            isGlobalMuteActive = !isGlobalMuteActive;
            String commandSenderName = sender instanceof Player ? ((Player) sender).getDisplayName() : "an higher instance";

            Bukkit.getOnlinePlayers()
                    .stream()
                    .map(CorePlayer::get)
                    .forEach(player -> player
                            .info("The chat was " + (isGlobalMuteActive ? "muted" : "demuted") +" by " + commandSenderName));


    }
}
