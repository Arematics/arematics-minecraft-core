package com.arematics.minecraft.gs;

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
        Bukkit.getConsoleSender().sendMessage("Bootstrap enabled, starting AdvancedGSSystem!");
        Engine.startEngine(PL);
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("Bootstrap Shutdown called, stopping AdvancedGSSystem!");
        Engine.shutdownEngine();
    }
}
