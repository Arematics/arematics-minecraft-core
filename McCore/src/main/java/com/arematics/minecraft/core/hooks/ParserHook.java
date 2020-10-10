package com.arematics.minecraft.core.hooks;

import com.arematics.minecraft.core.annotations.Parser;
import com.arematics.minecraft.core.command.processor.parser.CommandParameterParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.util.Set;

public class ParserHook extends PackageHook<Class<?>> {

    private String url;
    private ClassLoader classLoader;

    @Override
    void startPathHook(String url, ClassLoader loader, JavaPlugin plugin) {
        try{
            this.url = url;
            this.classLoader = loader;
            Set<Class<?>> classes = startPreProcessor(loader, plugin);
            if(classes.isEmpty())
                plugin.getLogger().info("Could not find any Parser");
            classes.forEach(classprocess -> processAction(classprocess, plugin));
        }catch (Exception e){
            plugin.getLogger().info("Could not find any Parser: " + e.getMessage());
        }
    }

    @Override
    public Set<Class<?>> startPreProcessor(ClassLoader loader, JavaPlugin plugin) {
        Reflections reflections = new Reflections(ScanEnvironment.getBuilder(this.url, this.classLoader));
        return reflections.getTypesAnnotatedWith(Parser.class);
    }

    @Override
    public void processAction(Class<?> o, JavaPlugin plugin) {
        if(isHookingDisabledFor(o)) return;
        try {
            CommandParameterParser<?> instance = (CommandParameterParser<?>) o.getConstructor().newInstance();
            Bukkit.getLogger().info("Adding " + instance.getClass().getName() + " as Parser");

            com.arematics.minecraft.core.command.processor.parser.Parser.getInstance().addParser(instance);
        }catch (Exception e){
            Bukkit.getLogger().severe("Could not register Parser: " + o.getName());
            e.printStackTrace();
        }
    }
}
