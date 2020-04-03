package com.arematics.minecraft.gs.listener;

import com.arematics.minecraft.gs.region.Region;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class AreaEnterListener implements Listener {

    private static Map<Player, Region> inRegion = new HashMap<>();
    private static Map<Player, Long> blocked = new HashMap<>();

    @EventHandler
    public void onEnter(PlayerMoveEvent event){
        Player player = event.getPlayer();

        Location from = event.getFrom();
        Location to = event.getTo();

        if(positionChange(from, to)){
            TourchClickListener.regions.forEach(region -> region.getConnections().forEach(connection -> {
                if(match(player, player.getLocation(), connection.getOne(), connection.getTwo())){
                    if(!blocked.containsKey(player) || (System.currentTimeMillis() - blocked.get(player) > 0)){
                        blocked.remove(player);
                        checkPlayer(player, region);
                        blocked.put(player, System.currentTimeMillis() + 1000);
                    }
                }
            }));
        }
    }

    private static boolean match(Player player, Location play, Location start, Location end){

        double px = play.getX();
        double pz = play.getZ();

        double sx = start.getX();
        double sz = start.getZ();

        double ex = end.getX();
        double ez = end.getZ();

        double rsx = getS(player, sx, (ex - sx), px);
        double rsz = getS(player, sz, (ez - sz), pz);

        return (rsx > 0 && rsz > 0) && (rsx <= 1 && rsz <= 1);
    }

    private static double getS(Player player, double c1, double c2, double c3){
        if(c2 == 0) return (c3 - c1);
        return (c3 - c1)/c2;
    }

    private static boolean positionChange(Location from, Location to){
        return from.getX() != to.getX() || from.getZ() != to.getZ();
    }

    private static boolean xMatch(Location one, Location two, Location loc){
        Vector lon = loc.toVector().subtract(one.toVector());
        Vector lot = loc.toVector().subtract(two.toVector());

        System.out.println("LON1: " + lon);
        System.out.println("LOT1: " + lot);

        if(lon.getX() < 0.07 && lon.getX() > -0.08){
            System.out.println("LON X YES");
            if(lon.getY() >= 0 && lot.getY() < 0){
                System.out.println("LON Y YES");
                return true;
            }else return lon.getY() < 0 && lot.getY() > 0;
        }else if(lot.getX() < 0.07 && lot.getX() > -0.08){
            System.out.println("LOT X YES");
            if(lot.getY() >= 0 && lon.getY() < 0){
                System.out.println("LOT Y YES");
                return true;
            }else return lot.getY() < 0 && lon.getY() > 0;
        }

        return false;
    }

    private static boolean yMatch(Location one, Location two, Location loc){
        Vector lon = loc.toVector().subtract(one.toVector());
        Vector lot = loc.toVector().subtract(two.toVector());

        System.out.println("LON2: " + lon);
        System.out.println("LOT2: " + lot);

        if(lon.getZ() < 0.07 && lon.getZ() > -0.08){
            System.out.println("LON Z YES");
            if(lon.getX() >= 0 && lot.getX() < 0){
                System.out.println("LON X YES");
                return true;
            }else return lon.getX() < 0 && lot.getX() > 0;
        }else if(lot.getZ() < 0.07 && lot.getZ() > -0.08){
            System.out.println("LOT Z YES");
            if(lot.getX() >= 0 && lon.getX() < 0){
                System.out.println("LOT X YES");
                return true;
            }else return lot.getX() < 0 && lon.getX() > 0;
        }

        return false;
    }

    private static void checkPlayer(Player player, Region region){
        if(inRegion.containsKey(player)){
            Region region1 = inRegion.get(player);
            if(region1.getName().equals(region.getName())){
                player.sendMessage("Leaving Region " + region.getName());
                inRegion.remove(player);
            }else{
                player.sendMessage("DAFUQ?");
            }
        }else{
            player.sendMessage("Enter Region " + region.getName());
            inRegion.put(player, region);
        }
    }
}
