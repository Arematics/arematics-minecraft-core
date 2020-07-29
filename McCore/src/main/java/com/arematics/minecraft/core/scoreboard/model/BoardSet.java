package com.arematics.minecraft.core.scoreboard.model;

import com.arematics.minecraft.core.scoreboard.functions.BoardPackets;
import com.arematics.minecraft.core.scoreboard.functions.Boards;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class BoardSet {

    public final Player PLAYER;
    public final BoardPackets PACKETS;
    public final Map<String, Board> BOARDS = new HashMap<>();
    public final Scoreboard SCOREBOARD;
    private Board shown = null;

    public BoardSet(Player p){
        this.PLAYER = p;
        this.PACKETS = new BoardPackets(p);
        this.SCOREBOARD = new Scoreboard();
        Boards.addBoard(this);
    }

    Scoreboard getScoreboard(){
        return SCOREBOARD;
    }

    public void remove(){
        BOARDS.values().forEach(Board::remove);
        Boards.remove(this);
    }

    public Board getBoard(String id){
        return BOARDS.getOrDefault(id, null);
    }

    public Board getOrAddBoard(String id, String displayname) {
        if(displayname == null) displayname = "";
        Board b;
        if((b = getBoard(id)) != null) b.setDisplayName(displayname);
        else b = addBoard(id, displayname);
        return b;
    }

    public Board addBoard(String id, String displayname){
        if(getBoard(id) != null) return null;
        Board b = new Board(id, this);
        ScoreboardObjective obj = SCOREBOARD.registerObjective(b.getObjectiveName(), IScoreboardCriteria.b);
        obj.setDisplayName(displayname);
        this.PACKETS.nmsSendObjective(obj);
        return b;
    }

    void onSetTeam(BoardEntry be){
        if(!isShown(be)) return;
        this.PACKETS.nmsSetTeam(be.getName(), be.getPrefix(), be.getSuffix(), getScoreboard());
    }

    void onTeamRemove(BoardEntry be){
        if(!isShown(be)) return;
        ScoreboardTeam t = SCOREBOARD.getPlayerTeam(be.getName());
        if(t == null) return;
        this.PACKETS.nmsRemoveTeam(t);
        SCOREBOARD.removeTeam(t);
    }

    void onSetScore(BoardEntry be){
        ScoreboardScore score = SCOREBOARD.getPlayerScoreForObjective(be.getName(), be.BOARD.getObjective());
        score.setScore(be.getScore());
        this.PACKETS.nmsSendScore(score);
    }

    void onScoreRemove(BoardEntry be){
        ScoreboardScore score = SCOREBOARD.getPlayerScoreForObjective(be.getName(), be.BOARD.getObjective());
        this.PACKETS.nmsRemoveScore(score);
        SCOREBOARD.resetPlayerScores(be.getName(), be.BOARD.getObjective());
    }

    public Board getShown() {
        return shown;
    }

    public boolean isShown(Board b){
        return b == shown;
    }

    public boolean isShown(BoardEntry entry){
        return isShown(entry.BOARD);
    }

    void onCurrentScoreboard(Board b){
        if(shown != null) for(BoardEntry be : shown.getEntries()) onTeamRemove(be);
        if(b == null){
            this.PACKETS.nmsDisplaySidebar(null);
            shown = null;
        }else{
            shown = b;
            for(BoardEntry e : shown.getEntries()) onSetTeam(e);
            this.PACKETS.nmsDisplaySidebar(b.getObjective());
        }
    }

    public Player getPlayer() {
        return PLAYER;
    }
}
