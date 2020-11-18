package com.arematics.minecraft.core.command.processor.parser;

import com.arematics.minecraft.core.server.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

@Component
public class CorePlayerParser extends CommandParameterParser<CorePlayer> {
    @Override
    public CorePlayer parse(String value) throws ParserException {
        Player player = Bukkit.getPlayer(value);
        if(player == null) throw new ParserException("Player " + value + " is not online");
        return CorePlayer.get(player);
    }
}
