package com.arematics.minecraft.ri.listener;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.ri.events.RegionEnteredEvent;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class MoveIntoNoPvPBlockListener implements Listener {

    @EventHandler
    public void onEnter(RegionEnteredEvent enteredEvent){
        CorePlayer player = enteredEvent.getPlayer();
        ProtectedRegion region = enteredEvent.getRegion();
        if(Objects.equals(region.getFlag(DefaultFlag.PVP), StateFlag.State.DENY) && player.inFight()){
            calc(player.getPlayer());
        }
    }

    private void calc(Player player){
        Location from = player.getLocation();
        Vector to = player.getLocation().getDirection();

        double fx = from.getX();
        double tx = to.getX();
        double fz = from.getZ();
        double tz = to.getZ();

        double vecX = 0.0;
        double vecZ = 0.0;

        if(fx < tx && from.getBlockX() != to.getBlockX()){
            vecX = 0.75;
        }else if(fx > tx && from.getBlockX() != to.getBlockX()){
            vecX = -0.75;
        }

        if(fz < tz  && from.getBlockZ() != to.getBlockZ()){
            vecZ = 0.75;
        }else if(fz > tz  && from.getBlockZ() != to.getBlockZ()){
            vecZ = -0.75;
        }

        Vector vec = new Vector(vecX, 0, vecZ);

        double hight = 0.45;

        from.add(vec.getX() * -1, 0, vec.getZ() * -1);
        if(from.getBlockX() == to.getBlockX() && from.getBlockY() == from.getBlockY() && from.getBlockZ() == to.getBlockZ()){
            hight = 0.6;
        }

        player.teleport(from);

        int multiplier = -1;

        vec.setX(vec.getX() * multiplier);
        vec.setY(hight);
        vec.setZ(vec.getZ() * multiplier);
        player.setVelocity(vec);
    }
}
