package com.arematics.minecraft.core.hooks;

import com.arematics.minecraft.core.annotations.PluginCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

public class CommandHooks extends PackageHook<Class<?>> {

    @Override
    void startPathHock(String url, ClassLoader loader, JavaPlugin plugin) {
        try{
            ScanEnvironment.getBuilder().getUrls().clear();
            ScanEnvironment.getBuilder().addUrls(ClasspathHelper.forPackage(url, loader));
            Set<Class<?>> classes = startPreProcessor(loader, plugin);
            if(classes.isEmpty())
                plugin.getLogger().warning("Could not find any Commands");
            classes.forEach(classprocess -> processAction(classprocess, plugin));
        }catch (Exception e){
            plugin.getLogger().warning("Could not find any Commands: " + e.getMessage());
        }
    }

    @Override
    public Set<Class<?>> startPreProcessor(ClassLoader loader, JavaPlugin plugin) {
        Reflections reflections = new Reflections(ScanEnvironment.getBuilder());
        return reflections.getTypesAnnotatedWith(PluginCommand.class);
    }

    @Override
    public void processAction(Class<?> o, JavaPlugin plugin) {
        try {
            CoreCommand instance = (CoreCommand) o.getConstructor().newInstance();
            Bukkit.getLogger().info("Adding " + instance.getClass().getName() + " as Command");

            instance.register();
        }catch (Exception e){
            Bukkit.getLogger().severe("Could not register Command: " + o.getName());
            e.printStackTrace();
        }
    }
}
