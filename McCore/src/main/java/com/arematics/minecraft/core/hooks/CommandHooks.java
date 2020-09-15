package com.arematics.minecraft.core.hooks;

import com.arematics.minecraft.core.annotations.DisableAutoHook;
import com.arematics.minecraft.core.annotations.PluginCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.util.Set;

public class CommandHooks extends PackageHook<Class<?>> {

    private String url;
    private ClassLoader classLoader;

    @Override
    void startPathHook(String url, ClassLoader loader, JavaPlugin plugin) {
        try{
            this.url = url;
            this.classLoader = loader;
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
        Reflections reflections = new Reflections(ScanEnvironment.getBuilder(this.url, this.classLoader));
        return reflections.getTypesAnnotatedWith(PluginCommand.class);
    }

    @Override
    public void processAction(Class<?> o, JavaPlugin plugin) {
        if(isHookingDisabledFor(o)) return;
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
