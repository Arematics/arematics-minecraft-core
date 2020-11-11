package com.arematics.minecraft.core.command.processor.parser;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.springframework.stereotype.Component;

@Component
public class OfflinePlayerParser extends CommandParameterParser<OfflinePlayer> {

    @Override
    public OfflinePlayer doParse(String value) throws ParserException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(value);
        if(player == null) throw new ParserException("Player with name: " + value + " could not be found");
        return player;
    }
}
