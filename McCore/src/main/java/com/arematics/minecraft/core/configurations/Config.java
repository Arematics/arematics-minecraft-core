package com.arematics.minecraft.core.configurations;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    private final Properties properties;

    public Config(File file) {
        this.properties = new Properties();
        try{
            properties.load(new FileInputStream(file));
        }catch (IOException ioe){
            Bukkit.getLogger().severe("Could not load configs: " + ioe.getMessage());
        }
    }

    public Properties getProperties() {
        return properties;
    }
}
