package com.arematics.minecraft.core.server;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CoreUtils {

    private static final Map<Player, CorePlayer> players = new HashMap<>();

    public static CorePlayer parsePlayer(Player player){
        if(!players.containsKey(player)){
            players.put(player, new CorePlayer(player));
        }

        return players.get(player);
    }
}
