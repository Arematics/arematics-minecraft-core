package com.arematics.minecraft.core.command.processor.parser;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.springframework.stereotype.Component;

@Component
public class OfflinePlayerParser extends CommandParameterParser<OfflinePlayer> {

    @Override
    public OfflinePlayer parse(String value) throws CommandProcessException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(value);
        if(player == null) throw new CommandProcessException("Player with name: " + value + " could not be found");
        return player;
    }
}
