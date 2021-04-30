package com.arematics.minecraft.core.server.entities.bossbar;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;

import java.lang.reflect.Field;

@Setter
@Getter
@AllArgsConstructor
public class BossBar {
    public static int CUSTOM_ID;

    static {
        try {
            String version = Bukkit.getServer().getClass().getName().split("\\.")[3];
            Field field = Class.forName("net.minecraft.server." + version + ".Entity").getDeclaredField("entityCount");
            field.setAccessible(true);
            CUSTOM_ID = field.getInt(null);
            field.set(null, CUSTOM_ID + 1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private final CorePlayer player;
    private String text;
    private int health;
    private Location location;
    private boolean spawned;

    public BossBar(CorePlayer player, String text, int health){
        this.player = player;
        this.text = text;
        this.health = health;
    }

    public WrappedDataWatcher getDataWatcher() {
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(0, (byte) 0x20); // invisible
        watcher.setObject(1, (short) 300); // air ticks
        watcher.setObject(2, this.text); // name
        watcher.setObject(3, (byte) 1); // show name
        watcher.setObject(6, (float) this.health, true); // Set health
        watcher.setObject(7, Color.BLACK.asRGB()); // potion color
        watcher.setObject(8, (byte) 0); // potion ambient
        watcher.setObject(15, (byte) 1); // No AI
        watcher.setObject(20, 819); // Invulnerable
        return watcher;
    }
}
