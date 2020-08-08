package com.arematics.minecraft.core.scoreboard.model;

import java.util.*;

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

    @Override
    public String toString() {
        return "Board{" +
                "BOARD_ID='" + BOARD_ID + '\'' +
                ", OBJECTIVE_NAME='" + OBJECTIVE_NAME + '\'' +
                ", ENTRIES=" + ENTRIES +
                ", LAST_UPDATE_TIME=" + LAST_UPDATE_TIME +
                ", ENTRY_DATA=" + ENTRY_DATA +
                ", MODERN_BOARD=" + MODERN_BOARD +
                '}';
    }
}
