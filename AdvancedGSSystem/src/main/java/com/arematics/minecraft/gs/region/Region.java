package com.arematics.minecraft.gs.region;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Region {

    private final String name;
    private final List<Connection> connections = new ArrayList<>();

    public Region(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public static class Connection{

        private final Location one;
        private final Location two;
        private final Vector vector;

        public Connection(Location one, Location two, Vector vector){
            this.one = one;
            this.two = two;
            this.vector = vector;
        }

        public Location getOne() {
            return one;
        }

        public Location getTwo() {
            return two;
        }

        public Vector getVector() {
            return vector;
        }
    }
}
