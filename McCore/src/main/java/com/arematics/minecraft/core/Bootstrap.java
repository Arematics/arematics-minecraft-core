package com.arematics.minecraft.core;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Bootstrap Class starting the Java Plugin an sending Startup or Shutdown Message to Engine.
 */
public class Bootstrap extends JavaPlugin {

    static Bootstrap PL;

    @Override
    public void onEnable() {
        PL = this;
        PL.getLogger().info("Bootstrap enabled, starting Engine!");
        Engine.startEngine(PL);
    }

    @Override
    public void onDisable() {
        PL.getLogger().info("Bootstrap Shutdown called, stopping Engine!");
        Engine.shutdownEngine();
    }
}
