package com.arematics.minecraft.core.command.processor.parser;

import com.arematics.minecraft.core.server.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

@Component
public class CorePlayerParser extends CommandSenderParser<CorePlayer> {

    @Override
    public CorePlayer parse(String value) throws CommandProcessException {
        Player player = Bukkit.getPlayer(value);
        if(player == null) throw new CommandProcessException("Player " + value + " is not online");
        return CorePlayer.get(player);
    }

    @Override
    public CorePlayer parse(CommandSender sender) throws CommandProcessException {
        try{
            return parse(sender.getName());
        }catch (CommandProcessException cpe){
            throw new CommandProcessException("Only Players could perform this command");
        }
    }
}
