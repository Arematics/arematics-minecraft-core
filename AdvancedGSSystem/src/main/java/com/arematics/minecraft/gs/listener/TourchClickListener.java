package com.arematics.minecraft.gs.listener;

import com.arematics.minecraft.gs.region.Region;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class TourchClickListener implements Listener {

    public static List<Region> regions = new ArrayList<>();

    private static Location one = null;
    private static Location two = null;

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.REDSTONE_TORCH_ON){
            if(one == null) one = event.getClickedBlock().getLocation().add(0.5, 0.5, 0.5);
            else if(two == null){
                two = event.getClickedBlock().getLocation().add(0.5, 0.5, 0.5);

                double distance = one.distance(two);

                Vector point1 = one.toVector();
                Vector point2 = two.toVector();

                Vector a = point2.clone().subtract(point1);
                Region region = new Region("test");
                region.getConnections().add(new Region.Connection(one, two, a));
                regions.add(region);
                Vector vector = a.clone().normalize().multiply(0.1);

                /*The distance covered*/
                double covered = 0;

                /* We run this code while we haven't covered the distance, we increase the point by the space every time*/
                for (; covered < distance;) {
                    event.getClickedBlock().getWorld().spigot()
                            .playEffect(point1.toLocation(event.getClickedBlock().getWorld()), Effect.COLOURED_DUST,
                                    0, 1, 255, 0, 0, 1, 0, 64);
                    point1.add(vector);
                    /* We add the space covered */
                    covered += 0.1;
                }

                one = null;
                two = null;
            }
        }
    }
}
