package com.arematics.minecraft.core.hooks;

import com.arematics.minecraft.core.configurations.Config;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ConfigHook {

    public static Config loadConfig(JavaPlugin plugin){
        File file = new File("plugins/" + plugin.getName() + "/config.yml");
        return new Config(file);
    }
}
