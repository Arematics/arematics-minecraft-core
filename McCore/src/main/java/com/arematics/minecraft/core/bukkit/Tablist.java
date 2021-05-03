package com.arematics.minecraft.core.bukkit;

import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.global.model.Rank;
import com.arematics.minecraft.data.service.RankService;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class Tablist {

    private final Server server;
    private final RankService rankService;

    @Autowired
    public Tablist(Server server, RankService rankService){
        this.server = server;
        this.rankService = rankService;
        this.rankService.findAll().forEach(this::patchTeam);
        flushOnlines();
    }

    public void patchTeam(Rank rank){
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(rank.getSortChar());
        if(team == null) team = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(rank.getSortChar());
        if(rank.isInTeam()) team.setPrefix(rank.getColorCode() + "§l" + rank.getName() + " | ");
        else team.setPrefix(rank.getColorCode());
        team.setAllowFriendlyFire(true);
        team.setCanSeeFriendlyInvisibles(false);
    }

    public void refreshTeams(){
        this.rankService.findAll().forEach(this::patchTeam);
    }

    public void flushOnlines(){
        refresh(Bukkit.getOnlinePlayers().stream().map(server.players()::fetchPlayer).toArray(CorePlayer[]::new));
    }

    public void refresh(CorePlayer... players){
        Arrays.stream(players).forEach(this::addToBoard);
    }

    public void remove(CorePlayer... players){
        Arrays.stream(players).forEach(this::remFromBoard);
    }

    private void addToBoard(CorePlayer player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        scoreboard.getTeam(player.getTopLevel().getSortChar()).addEntry(player.getPlayer().getDisplayName());
    }

    private void remFromBoard(CorePlayer player){
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        scoreboard.getTeam(player.getTopLevel().getSortChar()).removeEntry(player.getPlayer().getDisplayName());
    }
}
