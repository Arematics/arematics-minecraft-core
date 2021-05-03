package com.arematics.minecraft.core.command.processor.parser;

import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class CorePlayerParser extends CommandSenderParser<CorePlayer> {

    private final Server server;

    @Override
    public CorePlayer parse(String value) throws CommandProcessException {
        Player player = Bukkit.getPlayer(value);
        if(player == null) throw new CommandProcessException("Player " + value + " is not online");
        return server.players().fetchPlayer(player);
    }

    @Override
    public CorePlayer parse(CorePlayer sender) throws CommandProcessException {
        return sender;
    }
}
