package com.arematics.minecraft.gs;

import com.arematics.minecraft.core.hooks.CommandHooks;
import com.arematics.minecraft.core.hooks.ListenerHook;
import com.arematics.minecraft.core.hooks.ScanEnvironment;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Set;

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

        String url ="com.arematics.minecraft.gs";
        CommandHooks.hookCommands(url, this.getClass().getClassLoader(), bootstrap);
        ListenerHook.hookListeners(url, this.getClass().getClassLoader(), bootstrap);
    }

    public Bootstrap getPlugin() {
        return plugin;
    }
}
