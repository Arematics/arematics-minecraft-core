package com.arematics.minecraft.core.hooks;

import com.arematics.minecraft.core.language.LanguageAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class LanguageHook implements Hook<File>{

    private File file;

    @Override
    public void startHook(ClassLoader loader, JavaPlugin plugin) {
        file = new File("plugins/" + plugin.getName() + "/language");
        Set<File> processed = startPreProcessor(loader, plugin);
        processed.forEach(file -> processAction(file, plugin));
    }

    @Override
    public Set<File> startPreProcessor(ClassLoader loader, JavaPlugin plugin) {
        File[] files = file.listFiles();
        if(files != null)
            return Arrays.stream(files).filter(f -> f.getName().endsWith(".properties")).collect(Collectors.toSet());
        else
            return new HashSet<>();
    }

    @Override
    public void processAction(File file, JavaPlugin plugin) {
        try(FileInputStream stream = new FileInputStream(file)){
            LanguageAPI.registerFile(stream);
        }catch (Exception e){
            e.printStackTrace();
            Bukkit.getLogger().severe("Could not add File " + file.getName() + ": " + e.getMessage());
        }
    }
}
