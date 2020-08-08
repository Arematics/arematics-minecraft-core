package com.arematics.minecraft.core.scoreboard.functions;

import com.arematics.minecraft.core.scoreboard.model.Board;
import com.arematics.minecraft.core.scoreboard.model.BoardEntry;
import com.arematics.minecraft.core.scoreboard.model.BoardEntryData;
import net.minecraft.server.v1_8_R3.ScoreboardObjective;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BoardHandler {

    public final Board BOARD;
    private final BoardSet BOARD_SET;

    private boolean buildEntries = false;

    public BoardHandler(BoardSet set, Board board){
        this.BOARD_SET = set;
        this.BOARD = board;
    }

    public void toggleModernBoard(){
        this.BOARD.MODERN_BOARD = !this.BOARD.MODERN_BOARD;
        this.buildEntries = false;
        hide();
        show();
    }

    public void show(){
        buildEntries();
        this.BOARD_SET.enableScoreboard(this.BOARD);
    }

    public void hide(){
        this.BOARD_SET.enableScoreboard(null);
    }

    public void toggle(){
        if(isShown())
            hide();
        else
            show();
    }

    public boolean isShown(){
        return this.BOARD_SET.isShown(this.BOARD);
    }

    public ScoreboardObjective getObjective(){
        return this.BOARD_SET.SCOREBOARD.getObjective(this.BOARD.OBJECTIVE_NAME);
    }

    public BoardHandler setDisplayName(String displayname){
        System.out.println(this.BOARD);
        System.out.println(this.BOARD_SET.getScoreboard().getObjective(this.BOARD.OBJECTIVE_NAME));
        this.BOARD_SET.getScoreboard().getObjective(this.BOARD.OBJECTIVE_NAME).setDisplayName(displayname);
        this.BOARD_SET.PACKETS.nmsUpdateObjective(getObjective());
        this.BOARD.LAST_UPDATE_TIME = System.currentTimeMillis();
        return this;
    }

    public BoardHandler addEntryData(String name, String prefix, String suffix){
        this.BOARD.ENTRY_DATA.add(new BoardEntryData(name, prefix, suffix));
        return this;
    }

    public void buildEntries(){
        BoardEntryData[] dataSet = this.BOARD.ENTRY_DATA.toArray(new BoardEntryData[]{});
        System.out.println("Data Entries: " + dataSet.length);
        for(int i = dataSet.length - 1; i >= 0; i--){
            BoardEntryData data = dataSet[i];
            System.out.println("Data Entry: " + data);
            if(this.BOARD.MODERN_BOARD){
                int multiplied = (i + 1) * 2;
                int first = multiplied / 10;
                int second = multiplied % 10;
                String boardEntrySecondName = "§" + first + "§" + second + "§a";
                BoardEntry entry;
                BoardEntry entrySecond;
                try{
                    entry = addEntry(data.NAME + 1, multiplied, data.NAME);
                    entrySecond = addEntry(data.NAME + 2, multiplied - 1, boardEntrySecondName);
                }catch (Exception ignore){
                    entry = getEntry(data.NAME + 1);
                    entrySecond = getEntry(data.NAME + 2);
                }
                this.BOARD_SET.onSetTeam(entry, data.NAME, data.PREFIX, "");
                this.BOARD_SET.onSetTeam(entrySecond, boardEntrySecondName, "", data.SUFFIX);
            }else{
                BoardEntry entry;
                try{
                    entry = addEntry(data.NAME + 1, i, data.NAME);
                }catch (Exception ignore){
                    entry = getEntry(data.NAME + 1);
                }
                this.BOARD_SET.onSetTeam(entry, data.NAME, data.PREFIX, data.SUFFIX);
            }
        }
        this.buildEntries = true;
    }

    public BoardEntryData getDataByEntryName(BoardEntry boardEntry){
        return this.BOARD.ENTRY_DATA.stream()
                .filter(data -> data.NAME.equals(boardEntry.NAME))
                .findFirst()
                .orElse(new BoardEntryData(boardEntry.NAME, "", ""));
    }

    private BoardEntry addEntry(String id, int initialScore, String name){
        if(getEntry(id) != null) throw new IllegalArgumentException("A BoardEntry with id '" + id + "' was already created!");
        BoardEntry entry = new BoardEntry(this.BOARD, name, id, initialScore);
        this.BOARD.ENTRIES.put(id, entry);
        this.BOARD_SET.onSetScore(entry);
        this.BOARD.LAST_UPDATE_TIME = System.currentTimeMillis();
        return entry;
    }

    private BoardEntry getEntry(String id){
        return this.BOARD.ENTRIES.getOrDefault(id, null);
    }

    public void remove(){
        if(this.BOARD_SET.isShown(this.BOARD)) this.hide();
        Collection<BoardEntry> entries = new ArrayList<>(this.BOARD.ENTRIES.values());
        entries.forEach(this::removeEntry);
        this.BOARD_SET.PACKETS.nmsRemoveObjective(getObjective());
        this.BOARD_SET.SCOREBOARD.unregisterObjective(getObjective());
        this.BOARD_SET.BOARDS.remove(this.BOARD.BOARD_ID);
        this.BOARD.LAST_UPDATE_TIME = System.currentTimeMillis();
    }

    private void removeEntry(BoardEntry entry){
        this.BOARD_SET.onScoreRemove(entry);
        this.BOARD_SET.onTeamRemove(entry);
        this.BOARD.ENTRIES.remove(entry.ENTRY_ID);
        this.BOARD.LAST_UPDATE_TIME = entry.LAST_UPDATE_TIME = System.currentTimeMillis();
    }
}
