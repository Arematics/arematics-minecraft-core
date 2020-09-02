package com.arematics.minecraft.core.scoreboard.functions;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Boards {

    private static final List<BoardSet> SETS = new ArrayList<>();

    public static BoardSet getBoardSet(Player p){
        return Boards.SETS.stream().filter(boardSet -> sameUUID(boardSet.getPlayer(), p))
                .findFirst().orElseGet(() -> addBoardSet(new BoardSet(p)));
    }

    private static BoardSet addBoardSet(BoardSet boardSet){
        Boards.SETS.add(boardSet);
        return boardSet;
    }

    public static void removeBoardSet(BoardSet boardSet){
        Boards.SETS.remove(boardSet);
    }

    private static boolean sameUUID(Player boardPlayer, Player player){
        return boardPlayer.getUniqueId().toString().equals(player.getUniqueId().toString());
    }
}
