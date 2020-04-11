package com.arematics.minecraft.core;

import com.arematics.minecraft.core.configurations.Config;
import com.arematics.minecraft.core.hooks.ConfigHook;
import com.arematics.minecraft.core.hooks.MultiHook;
import org.bukkit.Bukkit;

class Engine {

    private static Engine INSTANCE;

    /**
     * @return Instance Engine Object
     */
    public static Engine getInstance(){
        return INSTANCE;
    }

    /**
     * Generates new Engine Class as Instace
     * @param bootstrap JavaPlugin
     */
    static void startEngine(Bootstrap bootstrap){
        INSTANCE = new Engine(bootstrap);
    }

    /**
     * Stopping Engine and calling Shutdown Hooks
     */
    static void shutdownEngine(){
        if(INSTANCE != null){
            //TODO Fire Shutdown Hook
        }else{
            Bukkit.getLogger().severe("Instance not found System shutdown whiteout saving");
        }
    }

    private final Bootstrap plugin;
    private final Config config;

    /**
     * Hooking Config File
     * Starts the Multi Hook to get all Hooks loaded (Language, Commands, Listener)
     * @param bootstrap JavaPlugin
     */
    public Engine(Bootstrap bootstrap){
        this.plugin = bootstrap;
        this.config = ConfigHook.loadConfig(bootstrap);
        MultiHook.addHooks("com.arematics.minecraft.core", this.getClass().getClassLoader(), bootstrap);
    }

    public Bootstrap getPlugin() {
        return plugin;
    }

    public Config getConfig() {
        return config;
    }
}
