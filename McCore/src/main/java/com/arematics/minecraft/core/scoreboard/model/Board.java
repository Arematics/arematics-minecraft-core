package com.arematics.minecraft.core.scoreboard.model;

import net.minecraft.server.v1_8_R3.ScoreboardObjective;

import java.util.HashMap;
import java.util.Random;

public class Board {

    private final String boardid;
    public BoardSet SET;
    private final String objname;
    public HashMap<String, BoardEntry> ENTRIES = new HashMap<>();
    public long LAST_UPDATE_TIME;

    String getObjectiveName(){
        return objname;
    }

    public BoardSet getSet(){
        return SET;
    }

    public String getBoardId() {
        return boardid;
    }

    Board(String boardid, BoardSet set){
        this.boardid = boardid;
        this.SET = set;
        this.objname = "board_" + new Random(System.nanoTime()).nextInt(99999999);
        this.SET.BOARDS.put(boardid, this);
        this.LAST_UPDATE_TIME = System.currentTimeMillis();
    }

    public Board setDisplayName(String displayname){
        SET.getScoreboard().getObjective(objname).setDisplayName(displayname);
        this.SET.PACKETS.nmsUpdateObjective(getObjective());
        this.LAST_UPDATE_TIME = System.currentTimeMillis();
        return this;
    }

    public String getDisplayName(){
        return SET.getScoreboard().getObjective(objname).getDisplayName();
    }

    public void show(){
        if(SET.isShown(this)) return;
        SET.onCurrentScoreboard(this);
    }

    public void hide(){
        if(!SET.isShown(this)) return;
        SET.onCurrentScoreboard(null);
    }

    public boolean isShown(){
        return SET.isShown(this);
    }

    public void remove(){
        if(SET.isShown(this)) hide();
        for(BoardEntry e : getEntries()) e.remove();
        SET.PACKETS.nmsRemoveObjective(getObjective());
        SET.SCOREBOARD.unregisterObjective(getObjective());
        SET.BOARDS.remove(boardid);
        this.LAST_UPDATE_TIME = System.currentTimeMillis();
    }

    public BoardEntry addEntry(String id, String name, int initialScore, String prefix, String suffix){
        if(getEntry(id) != null) throw new IllegalArgumentException("A BoardEntry with id '" + id + "' was already created!");
        return new BoardEntry(id, this, name, initialScore, prefix, suffix);
    }

    public BoardEntry getEntry(String id){
        return ENTRIES.getOrDefault(id, null);
    }

    ScoreboardObjective getObjective(){
        return SET.SCOREBOARD.getObjective(objname);
    }

    public BoardEntry[] getEntries(){
        return ENTRIES.values().toArray(new BoardEntry[]{});
    }

    public long getLastUpdateTimestamp() {
        return this.LAST_UPDATE_TIME;
    }
}
