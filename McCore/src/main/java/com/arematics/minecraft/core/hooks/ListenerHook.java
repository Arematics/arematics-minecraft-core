package com.arematics.minecraft.core.hooks;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.Set;

public class ListenerHook{

    public static void hookListeners(ScanEnvironment environment){
        try{
            Set<Method> methods = environment.getScannedMethods(EventHandler.class);
            Bukkit.getLogger().info("Starting Listener Hook");
            methods.forEach(method -> processClass(method, environment.getPlugin()));
        }catch (Exception e){
            Bukkit.getLogger().warning("Could not find any Listeners in: " + environment.getPlugin().getName());
        }
    }

    private static void processClass(Method method, JavaPlugin plugin){
        Class<?> classprocess = method.getDeclaringClass();
        try {
            Object instance = classprocess.getConstructor().newInstance();
            Bukkit.getLogger().info("Adding " + classprocess.getName() + " as Listener");
            Bukkit.getPluginManager().registerEvents((org.bukkit.event.Listener) instance, plugin);
        }catch (Exception e){
            Bukkit.getLogger().severe("Could not register Listener: " + classprocess.getName());
            e.printStackTrace();
        }
    }
}
