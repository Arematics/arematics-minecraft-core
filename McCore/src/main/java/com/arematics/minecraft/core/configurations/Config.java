package com.arematics.minecraft.core.configurations;

import com.arematics.minecraft.core.Engine;
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

    public static Config getInstance(){
        return Engine.getInstance().getConfig();
    }

    private final Properties properties;
    private final Map<String, MessageHighlight> highlights = new HashMap<>();

    private final String commandPrefix;

    public Config(File file) {
        this.properties = new Properties();
        try(FileInputStream stream = new FileInputStream(file)){
            properties.load(stream);
        }catch (IOException ioe){
            Bukkit.getLogger().severe("Could not load configs: " + ioe.getMessage());
        }

        this.commandPrefix = findByKey("command_prefix") + findByKey("command_split");

        addMessageHighlight(SUCCESS);
        addMessageHighlight(WARNING);
        addMessageHighlight(FAILURE);
    }

    private void addMessageHighlight(String typo){

        String colorCodeSuccess = findByKey("command_" + typo + "_color");
        String soundSuccess = findByKey("command_" + typo + "_sound");
        addMessageHighlight(typo, colorCodeSuccess, soundSuccess);
    }

    private void addMessageHighlight(String name, String color, String sound){
        this.highlights.put(name, new MessageHighlight(color, Sound.valueOf(sound)));
    }

    public Properties getProperties() {
        return properties;
    }

    public String findByKey(String key){
        return this.properties.getProperty(key).replaceAll("'", "")
                .replaceAll("\"", "");
    }

    public Map<String, MessageHighlight> getHighlights() {
        return highlights;
    }

    public MessageHighlight getHighlight(String name){
        return getHighlights().get(name);
    }

    public String getPrefix(){
        return this.commandPrefix;
    }
}
