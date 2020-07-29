package com.arematics.minecraft.core.scoreboard.functions;

import com.arematics.minecraft.core.scoreboard.model.BoardSet;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Boards {

    private static final List<BoardSet> SETS = new ArrayList<>();

    public static BoardSet getSet(Player p){
        return SETS.stream().filter(boardSet -> sameUUID(boardSet.getPlayer(), p))
                .findFirst().orElse(new BoardSet(p));
    }

    public static boolean hasSet(Player p){
        return SETS.stream().anyMatch(boardSet -> sameUUID(boardSet.getPlayer(), p));
    }

    public static void addBoard(BoardSet boardSet){
        SETS.add(boardSet);
    }

    public static void remove(BoardSet boardSet){
        SETS.remove(boardSet);
    }

    private static boolean sameUUID(Player boardPlayer, Player player){
        return boardPlayer.getUniqueId().toString().equals(player.getUniqueId().toString());
    }
}
