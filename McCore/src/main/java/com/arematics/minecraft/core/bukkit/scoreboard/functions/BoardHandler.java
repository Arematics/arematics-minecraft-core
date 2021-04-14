package com.arematics.minecraft.core.bukkit.scoreboard.functions;

import com.arematics.minecraft.core.bukkit.scoreboard.model.Board;
import com.arematics.minecraft.core.bukkit.scoreboard.model.BoardEntry;
import com.arematics.minecraft.core.bukkit.scoreboard.model.BoardEntryData;
import net.minecraft.server.v1_8_R3.ScoreboardObjective;

import java.util.ArrayList;
import java.util.Collection;

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
        refresh();
    }

    public void show(){
        this.BOARD_SET.enableScoreboard(this.BOARD);
    }

    public void hide(){
        this.BOARD_SET.enableScoreboard(null);
    }

    @Deprecated
    public void refresh(){
        this.buildEntries = false;
        hide();
        show();
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
        this.BOARD_SET.getScoreboard().getObjective(this.BOARD.OBJECTIVE_NAME).setDisplayName(displayname);
        this.BOARD_SET.PACKETS.nmsUpdateObjective(getObjective());
        this.BOARD.LAST_UPDATE_TIME = System.currentTimeMillis();
        return this;
    }

    public BoardHandler addEntryData(String name, String prefix, String suffix){
        this.BOARD.ENTRY_DATA.add(new BoardEntryData(name, prefix, suffix));
        return this;
    }

    /**
     * Change Value of Scoreboard Entry.
     * You need to call buildEntries() Method after this, otherwise the value wouldn't be updated.
     * @param name Name of Scoreboard Entry
     * @param suffix Value to Change
     * @return Current Handler
     */
    public BoardHandler setEntrySuffix(String name, String suffix){
        System.out.println(this.BOARD);
        System.out.println(this.BOARD.ENTRY_DATA);
        this.BOARD.ENTRY_DATA
                .stream()
                .filter(data -> data.NAME.equals(name))
                .findFirst()
                .ifPresent(entryData -> updateSuffix(entryData, suffix));
        return this;

    }

    /**
     * Change Color of Scoreboard Entry.
     * You need to call buildEntries() Method after this, otherwise the value wouldn't be updated.
     * @param name Name of Scoreboard Entry
     * @param prefix Value to Change
     * @return Current Handler
     */
    public BoardHandler setEntryPrefix(String name, String prefix){
        this.BOARD.ENTRY_DATA
                .stream()
                .filter(data -> data.NAME.equals(name))
                .findFirst()
                .ifPresent(entryData -> updatePrefix(entryData, prefix));
        return this;

    }

    private void updatePrefix(BoardEntryData entryData, String prefix){
        entryData.PREFIX = prefix;
        BoardEntry entry = getEntry(entryData.NAME + 2);
        this.BOARD_SET.onSetTeam(entry, entry.NAME, entryData.PREFIX, "");
    }

    private void updateSuffix(BoardEntryData entryData, String suffix){
        entryData.SUFFIX = suffix;
        BoardEntry entry = getEntry(entryData.NAME + 3);
        this.BOARD_SET.onSetTeam(entry, entry.NAME, "", entryData.SUFFIX);
    }

    void buildEntries(){
        Collection<BoardEntry> entries = new ArrayList<>(this.BOARD.ENTRIES.values());
        entries.forEach(this::removeEntry);
        BoardEntryData[] dataSet = this.BOARD.ENTRY_DATA.toArray(new BoardEntryData[]{});
        for(int i = dataSet.length - 1; i >= 0; i--){
            BoardEntryData data = dataSet[i];
            if(this.BOARD.MODERN_BOARD){
                int multiplied = (i + 1) * 3;
                String spacerName = "§a" + "§" + multiplied / 10 + "§" + multiplied % 10 + "§a";
                String boardEntrySecondName = "§" + multiplied / 10 + "§" + multiplied % 10 + "§a";
                generateEntry(data.NAME + 1, multiplied, spacerName, "", "");
                generateEntry(data.NAME + 2, multiplied - 1, data.NAME, data.PREFIX, "");
                generateEntry(data.NAME + 3, multiplied - 2, boardEntrySecondName, "", data.SUFFIX);
            }else{
                generateEntry(data.NAME + 1, i, data.NAME, data.PREFIX, data.SUFFIX);
            }
        }
        this.buildEntries = true;
    }

    private void generateEntry(String id, int initialScore, String name, String prefix, String suffix){
        BoardEntry entry = getEntry(id);
        if(entry == null) entry = addEntry(id, initialScore, name);
        this.BOARD_SET.onSetTeam(entry, name, prefix, suffix);
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
