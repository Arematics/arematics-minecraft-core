package com.arematics.minecraft.core.scoreboard.functions;

import com.arematics.minecraft.core.scoreboard.model.Board;
import com.arematics.minecraft.core.scoreboard.model.BoardEntry;
import com.arematics.minecraft.core.scoreboard.model.BoardEntryData;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BoardSet {

    public final Player PLAYER;
    public final BoardPackets PACKETS;
    public final Map<String, Board> BOARDS = new HashMap<>();
    public final Scoreboard SCOREBOARD;
    public Board SHOWN = null;

    public BoardSet(Player p){
        System.out.println("Init new board");
        this.PLAYER = p;
        this.PACKETS = new BoardPackets(p);
        this.SCOREBOARD = new Scoreboard();
    }

    public Scoreboard getScoreboard(){
        return SCOREBOARD;
    }

    public void remove(){
        Collection<Board> boards = new ArrayList<>(this.BOARDS.values());
        boards.forEach(board -> new BoardHandler(this, board).remove());
        Boards.removeBoardSet(this);
    }

    private ScoreboardObjective fetchObjective(BoardEntry entry){
        return this.SCOREBOARD.getObjective(entry.BOARD.OBJECTIVE_NAME);
    }

    public BoardHandler getBoard(String id){
        return new BoardHandler(this, BOARDS.getOrDefault(id, null));
    }

    public BoardHandler getOrAddBoard(String id, String displayname) {
        if(displayname == null) displayname = "";
        BoardHandler handler;
        if((handler = getBoard(id)).BOARD != null) handler.setDisplayName(displayname);
        else handler = addBoard(id, displayname);
        return handler;
    }

    public BoardHandler addBoard(String id, String displayname){
        if(getBoard(id).BOARD != null) return null;
        Board board = new Board(id);
        this.BOARDS.put(id, board);
        ScoreboardObjective obj = SCOREBOARD.registerObjective(board.OBJECTIVE_NAME, IScoreboardCriteria.b);
        obj.setDisplayName(displayname);
        this.PACKETS.nmsSendObjective(obj);
        return new BoardHandler(this, board);
    }

    void onSetTeam(BoardEntry boardEntry, String name, String prefix, String suffix){
        if(isNotShown(boardEntry)) return;
        this.PACKETS.nmsSetTeam(name, prefix, suffix, getScoreboard());
    }

    void onTeamRemove(BoardEntry boardEntry){
        if(isNotShown(boardEntry)) return;
        ScoreboardTeam t = SCOREBOARD.getPlayerTeam(boardEntry.NAME);
        if(t == null) return;
        this.PACKETS.nmsRemoveTeam(t);
        this.SCOREBOARD.removeTeam(t);
    }

    void onSetScore(BoardEntry boardEntry){
        ScoreboardScore score = SCOREBOARD.getPlayerScoreForObjective(boardEntry.NAME, fetchObjective(boardEntry));
        score.setScore(boardEntry.SCORE);
        this.PACKETS.nmsSendScore(score);
    }

    void onScoreRemove(BoardEntry boardEntry){
        ScoreboardScore score = SCOREBOARD.getPlayerScoreForObjective(boardEntry.NAME, fetchObjective(boardEntry));
        this.PACKETS.nmsRemoveScore(score);
        this.SCOREBOARD.resetPlayerScores(boardEntry.NAME, fetchObjective(boardEntry));
    }

    public Board getShown() {
        return SHOWN;
    }

    public boolean isShown(Board b){
        return b != null && b == this.SHOWN;
    }

    public boolean isNotShown(BoardEntry entry){
        return !isShown(entry.BOARD);
    }

    public void enableScoreboard(Board board){
        if(isShown(board)) return;
        if(this.SHOWN != null) this.SHOWN.ENTRIES.values().forEach(this::onTeamRemove);
        if(board == null){
            this.PACKETS.nmsDisplaySidebar(null);
            this.SHOWN = null;
            System.out.println("Hiding board");
        }else{
            this.SHOWN = board;
            BoardHandler handler = new BoardHandler(this, board);
            handler.buildEntries();
            this.PACKETS.nmsDisplaySidebar(handler.getObjective());
        }
    }

    public Player getPlayer() {
        return PLAYER;
    }
}
