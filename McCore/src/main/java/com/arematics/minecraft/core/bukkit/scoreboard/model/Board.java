package com.arematics.minecraft.core.bukkit.scoreboard.model;

import lombok.ToString;

import java.util.*;

@ToString
public class Board {

    public final String BOARD_ID;
    public final String OBJECTIVE_NAME;
    public HashMap<String, BoardEntry> ENTRIES = new HashMap<>();
    public long LAST_UPDATE_TIME = System.currentTimeMillis();
    public List<BoardEntryData> ENTRY_DATA = new LinkedList<>();
    public boolean MODERN_BOARD = true;

    public Board(String boardid){
        this.BOARD_ID = boardid;
        this.OBJECTIVE_NAME = "board_" + new Random(System.nanoTime()).nextInt(99999999);
    }
}
