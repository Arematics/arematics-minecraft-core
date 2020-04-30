package com.arematics.minecraft.core;

import com.arematics.minecraft.core.command.parser.Parser;
import com.arematics.minecraft.core.configurations.Config;
import com.arematics.minecraft.core.hooks.*;
import org.bukkit.Bukkit;

public class Engine {

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
    private final Parser parser;

    /**
     * Hooking Config File
     * Starts the Multi Hook to get all Hooks loaded (Language, Commands, Listener)
     * @param bootstrap JavaPlugin
     */
    public Engine(Bootstrap bootstrap){
        this.plugin = bootstrap;
        MultiHook hook = new MultiHook("com.arematics.minecraft.core", this.getClass().getClassLoader(), bootstrap);
        hook.addPreHook(new PreFileExistHook());
        hook.addHook(new LanguageHook());
        hook.addPackageHook(new CommandHooks(), new ListenerHook());
        hook.enable();
        this.config = ConfigHook.loadConfig(bootstrap);
        this.parser = new Parser();
    }

    public Bootstrap getPlugin() {
        return plugin;
    }

    public Config getConfig() {
        return config;
    }

    public Parser getParser() {
        return parser;
    }
}
