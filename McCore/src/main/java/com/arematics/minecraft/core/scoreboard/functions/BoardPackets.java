package com.arematics.minecraft.core.scoreboard.functions;

import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.codec.digest.Md5Crypt;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Random;

public class BoardPackets {

    private static final int SEND = 0;
    private static final int UPDATE = 2;
    private static final int REMOVE = 1;

    private final Player PLAYER;

    public BoardPackets(Player player){
        this.PLAYER = player;
    }

    public void nmsSendObjective(ScoreboardObjective obj){
        PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective(obj, SEND);
        ((CraftPlayer) this.PLAYER).getHandle().playerConnection.sendPacket(packet);
    }

    public void nmsUpdateObjective(ScoreboardObjective obj){
        PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective(obj, UPDATE);
        ((CraftPlayer) PLAYER).getHandle().playerConnection.sendPacket(packet);
    }

    public void nmsRemoveObjective(ScoreboardObjective obj){
        PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective(obj, REMOVE);
        ((CraftPlayer) PLAYER).getHandle().playerConnection.sendPacket(packet);
    }

    public void nmsSendScore(ScoreboardScore score){
        PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore(score);
        ((CraftPlayer) PLAYER).getHandle().playerConnection.sendPacket(packet);
    }

    public void nmsRemoveScore(ScoreboardScore score){
        PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore(score.getPlayerName());
        ((CraftPlayer) PLAYER).getHandle().playerConnection.sendPacket(packet);
    }

    /*
     * obj = null sollte die Sidebar entfernen
     */
    public void nmsDisplaySidebar(ScoreboardObjective obj){
        PacketPlayOutScoreboardDisplayObjective packet = new PacketPlayOutScoreboardDisplayObjective(1, obj);
        ((CraftPlayer) PLAYER).getHandle().playerConnection.sendPacket(packet);
    }

    public void nmsRemoveTeam(ScoreboardTeam team){
        if(team == null) return;
        PacketPlayOutScoreboardTeam delteam = new PacketPlayOutScoreboardTeam(team, REMOVE);
        ((CraftPlayer) PLAYER).getHandle().playerConnection.sendPacket(delteam);
    }


    public void nmsSetTeam(String entry, String prefix, String suffix, Scoreboard scoreboard){
        if(prefix == null) prefix = "";
        if(suffix == null) suffix = "";

        ScoreboardTeam t;
        if((t = scoreboard.getPlayerTeam(entry)) == null){
            String teamName = new Random().nextLong() + System.currentTimeMillis() + System.nanoTime() + "";
            t = scoreboard.createTeam("team-" + Md5Crypt.md5Crypt(teamName.getBytes()).substring(0, 10));
            t.setPrefix(prefix);
            t.setSuffix(suffix);
            t.setAllowFriendlyFire(true);
            t.setCanSeeFriendlyInvisibles(false);
            t.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS);
            scoreboard.addPlayerToTeam(entry, t.getName());
            //System.out.println("Team | Name: " + t.getName() + " | Players: " + t.getPlayerNameSet() + " | Prefix: " + t.getPrefix().replace("§", "&") + " | Suffix: " + t.getSuffix().replace("§", "&"));
            PacketPlayOutScoreboardTeam newteam = new PacketPlayOutScoreboardTeam(t, SEND);
            ((CraftPlayer) PLAYER).getHandle().playerConnection.sendPacket(newteam);
        }else{
            t.setPrefix(prefix);
            t.setSuffix(suffix);
            PacketPlayOutScoreboardTeam editteam = new PacketPlayOutScoreboardTeam(t, UPDATE);
            ((CraftPlayer) PLAYER).getHandle().playerConnection.sendPacket(editteam);

        }
    }
}
