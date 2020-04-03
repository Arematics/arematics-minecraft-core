package com.arematics.minecraft.core.hooks;

import com.arematics.minecraft.core.annotations.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import java.util.Arrays;
import java.util.Set;

public class CommandHooks {

    /**
     * Clearing Urls of Configuration Builder and adds the new url starting Reflection Scan and reads all Classes
     * annotated with  {@link com.arematics.minecraft.core.annotations.Command}
     * Needs {@link com.arematics.minecraft.core.command.CoreCommand} as extends so all Command Names could be added
     * @param url Package Path
     * @param loader ClassLoader where hookCommands is called
     * @param plugin JavaPlugin Class where Commands should be added
     */
    public static void hookCommands(String url, ClassLoader loader, JavaPlugin plugin){
        try{
            ScanEnvironment.getBuilder().getUrls().clear();
            ScanEnvironment.getBuilder().addUrls(ClasspathHelper.forPackage(url, loader));
            Reflections reflections = new Reflections(ScanEnvironment.getBuilder());
            Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Command.class);
            Bukkit.getLogger().info("Starting Command Hook");
            classes.forEach(classprocess -> processClass(classprocess, plugin));
        }catch (Exception e){
            Bukkit.getLogger().warning("Could not find any Commands in: " + plugin.getName());
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
