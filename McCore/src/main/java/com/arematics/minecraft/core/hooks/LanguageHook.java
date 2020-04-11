package com.arematics.minecraft.core.hooks;

import com.arematics.minecraft.core.language.LanguageAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

public class LanguageHook {

    public static void addLanguageFiles(JavaPlugin plugin){
        File file = new File("plugins/" + plugin.getName() + "/language/");
        if(file.exists()){
            File[] files = file.listFiles();
            if(files != null)
                Arrays.stream(files).filter(f -> f.getName().endsWith(".properties")).forEach(LanguageHook::addFile);
        }
    }

    private static void addFile(File file){
        try{
            LanguageAPI.registerFile(new FileInputStream(file));
        }catch (Exception e){
            Bukkit.getLogger().severe("Could not add File " + file.getName() + ": " + e.getMessage());
        }
    }
}
