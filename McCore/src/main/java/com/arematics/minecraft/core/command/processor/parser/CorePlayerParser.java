package com.arematics.minecraft.core.command.processor.parser;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.Bukkit;
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
    public CorePlayer parse(CorePlayer sender) throws CommandProcessException {
        return sender;
    }
}
