package com.arematics.minecraft.core;

import com.arematics.minecraft.core.hooks.CommandHooks;
import com.arematics.minecraft.core.hooks.ListenerHook;
import com.arematics.minecraft.core.hooks.ScanEnvironment;
import org.bukkit.Bukkit;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

public class Engine {

    private static Engine INSTANCE;

    public static Engine getInstance(){
        return INSTANCE;
    }

    static void startEngine(Bootstrap bootstrap){
        INSTANCE = new Engine(bootstrap);
    }

    static void shutdownEngine(){
        if(INSTANCE != null){
            //TODO Fire Shutdown Hook
        }else{
            Bukkit.getLogger().severe("Instance not found System shutdown whiteout saving");
        }
    }

    private Bootstrap plugin;

    public Engine(Bootstrap bootstrap){
        this.plugin = bootstrap;
        ScanEnvironment environment = new ScanEnvironment("com.arematics.minecraft.core", bootstrap);
        CommandHooks.hookCommands(environment);
        ListenerHook.hookListeners(environment);
    }

    public Bootstrap getPlugin() {
        return plugin;
    }
}
