package com.arematics.minecraft.core.bukkit.scoreboard.model;

import lombok.ToString;

@ToString
public class BoardEntry {

    @ToString.Exclude
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
}
