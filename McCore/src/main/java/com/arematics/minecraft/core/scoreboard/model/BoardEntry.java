package com.arematics.minecraft.core.scoreboard.model;

public class BoardEntry {

    public final Board BOARD;
    public final String NAME;
    public final String ENTRY_ID;
    public int SCORE;
    public long LAST_UPDATE_TIME;

    public BoardEntry(Board board, String name, String entryid, int initalScore){
        this.BOARD = board;
        this.NAME = name;
        this.ENTRY_ID = entryid;
        this.SCORE = initalScore;
        this.LAST_UPDATE_TIME = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "BoardEntry{" +
                "BOARD=" + BOARD +
                ", NAME='" + NAME + '\'' +
                ", ENTRY_ID='" + ENTRY_ID + '\'' +
                ", SCORE=" + SCORE +
                ", LAST_UPDATE_TIME=" + LAST_UPDATE_TIME +
                '}';
    }
}
