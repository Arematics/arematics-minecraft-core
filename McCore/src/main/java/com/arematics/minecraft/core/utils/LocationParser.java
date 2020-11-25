package com.arematics.minecraft.core.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationParser {

    public static String fromLocation(Location loc) {
        return loc.getWorld().getName() + ";" + loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" +
                loc.getYaw() + ";" + loc.getPitch();
    }

    public static Location toLocation(String loc) {
        String[] args = loc.split(";");
        if(args.length != 6) throw new RuntimeException("Invalid location string");

        return new Location(Bukkit.getWorld(args[0]),
                Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]),
                Float.parseFloat(args[4]), Float.parseFloat(args[5]));
    }
}
