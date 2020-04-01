package com.arematics.minecraft.core.hooks;

import com.arematics.minecraft.core.annotations.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Set;

public class CommandHooks {

    public static void hookCommands(ScanEnvironment environment){
        try{
            Set<Class<?>> classes = environment.getScannedClasses(Command.class);
            Bukkit.getLogger().info("Starting Command Hook");
            classes.forEach(classprocess -> processClass(classprocess, environment.getPlugin()));
        }catch (Exception e){
            Bukkit.getLogger().warning("Could not find any Commands in: " + environment.getPlugin().getName());
        }
    }

    private static void processClass(Class<?> classprocess, JavaPlugin plugin){
        try {
            Object instance = classprocess.getConstructor().newInstance();
            Bukkit.getLogger().info("Adding " + classprocess.getName() + " as Command");
            String[] result = (String[]) classprocess.getMethod("getCommandNames").invoke(instance);

            Arrays.stream(result).forEach(name -> plugin.getCommand(name).setExecutor((CommandExecutor)instance));
        }catch (Exception e){
            Bukkit.getLogger().severe("Could not register Command: " + classprocess.getName());
            e.printStackTrace();
        }
    }
}
