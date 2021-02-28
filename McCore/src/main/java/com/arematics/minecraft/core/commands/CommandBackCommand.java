package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

@Component
public class CommandBackCommand extends CoreCommand {

    public CommandBackCommand(){
        super("cmdback");
    }

    @Override
    public void onDefaultExecute(CommandSender sender) {
        if(!(sender instanceof Player)) return;
        CorePlayer player = CorePlayer.get((Player) sender);
        player.dispatchCommand(player.getLastCommand(3));
    }
}
