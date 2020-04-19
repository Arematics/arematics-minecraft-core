package com.arematics.minecraft.core.configurations;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Config {

    public static final String SUCCESS = "success";
    public static final String WARNING = "warning";
    public static final String FAILURE = "failure";

    private final Properties properties;
    private final Map<String, MessageHighlight> highlights = new HashMap<>();

    private final String commandPrefix;

    public Config(File file) {
        this.properties = new Properties();
        try{
            properties.load(new FileInputStream(file));
        }catch (IOException ioe){
            Bukkit.getLogger().severe("Could not load configs: " + ioe.getMessage());
        }

        this.commandPrefix = this.properties.getProperty("command_prefix") + this.properties.getProperty("command_split");

        addMessageHighlight(SUCCESS);
        addMessageHighlight(WARNING);
        addMessageHighlight(FAILURE);
    }

    private void addMessageHighlight(String typo){

        String colorCodeSuccess = this.properties.getProperty("command_" + typo + "_color");
        String soundSuccess = this.properties.getProperty("command_" + typo + "_sound");
        addMessageHighlight(typo, colorCodeSuccess, soundSuccess);
    }

    private void addMessageHighlight(String name, String color, String sound){
        this.highlights.put(name, new MessageHighlight(color, Sound.valueOf(sound)));
    }

    public Properties getProperties() {
        return properties;
    }

    public Map<String, MessageHighlight> getHighlights() {
        return highlights;
    }

    public String getPrefix(){
        return this.commandPrefix;
    }
}
