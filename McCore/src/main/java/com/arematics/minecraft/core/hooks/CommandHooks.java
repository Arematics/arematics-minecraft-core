package com.arematics.minecraft.core.hooks;

import com.arematics.minecraft.core.annotations.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import java.util.Arrays;
import java.util.Set;

public class CommandHooks extends PackageHook<Class<?>> {

    @Override
    void startPathHock(String url, ClassLoader loader, JavaPlugin plugin) {
        try{
            ScanEnvironment.getBuilder().getUrls().clear();
            ScanEnvironment.getBuilder().addUrls(ClasspathHelper.forPackage(url, loader));
            Set<Class<?>> classes = startPreProcessor(loader, plugin);
            Bukkit.getLogger().info("Starting Command Hook");
            classes.forEach(classprocess -> processAction(classprocess, plugin));
        }catch (Exception e){
            Bukkit.getLogger().warning("Could not find any Commands in: " + plugin.getName());
        }
    }

    @Override
    public Set<Class<?>> startPreProcessor(ClassLoader loader, JavaPlugin plugin) {
        Reflections reflections = new Reflections(ScanEnvironment.getBuilder());
        return reflections.getTypesAnnotatedWith(Command.class);
    }

    @Override
    public void processAction(Class<?> o, JavaPlugin plugin) {
        try {
            Object instance = o.getConstructor().newInstance();
            Bukkit.getLogger().info("Adding " + o.getName() + " as Command");
            String[] result = (String[]) o.getMethod("getCommandNames").invoke(instance);

            Arrays.stream(result).forEach(name -> plugin.getCommand(name).setExecutor((CommandExecutor)instance));
        }catch (Exception e){
            Bukkit.getLogger().severe("Could not register Command: " + o.getName());
            e.printStackTrace();
        }
    }
}
