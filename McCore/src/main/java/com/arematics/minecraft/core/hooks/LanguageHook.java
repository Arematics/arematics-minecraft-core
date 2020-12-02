package com.arematics.minecraft.core.hooks;

import com.arematics.minecraft.core.language.LanguageAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.io.InputStream;
import java.util.Set;
import java.util.regex.Pattern;

public class LanguageHook implements Hook<String>{

    private ClassLoader loader;

    @Override
    public void startHook(ClassLoader loader, JavaPlugin plugin) {
        this.loader = loader;
        Set<String> processed = startPreProcessor(loader, plugin);
        processed.forEach(file -> processAction(file, plugin));
    }

    @Override
    public Set<String> startPreProcessor(ClassLoader loader, JavaPlugin plugin) {
        Reflections reflections = new Reflections("language", new ResourcesScanner());
        return reflections.getResources(Pattern.compile(".*"));
    }

    @Override
    public void processAction(String file, JavaPlugin plugin) {
        try(InputStream stream = this.loader.getResourceAsStream(file)){
            LanguageAPI.registerFile(stream);
        }catch (Exception e){
            e.printStackTrace();
            Bukkit.getLogger().severe("Could not add Language File " + file + ": " + e.getMessage());
        }
    }
}
