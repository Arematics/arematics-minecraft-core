package com.arematics.minecraft.core;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class Bootstrap extends JavaPlugin {

    public abstract Bootstrap getPlugin();
    public abstract BaseEngine getEngine() throws Exception;

    @Override
    public void onEnable() {
        System.setProperty("file.encoding", "UTF-8");
        getPlugin().getLogger().info("Bootstrap enabled, starting Engine!");
        try{
            Engine.addEngine(getEngine());
        }catch (Exception e){
            getPlugin().getLogger().severe("Engine startup failed. Stopping Plugin");
            onDisable();
        }
    }

    @Override
    public void onDisable() {
        getPlugin().getLogger().info("Bootstrap Shutdown called, stopping Engine!");
        try{
            Engine.shutdownEngine(getEngine().getClass());
        }catch (Exception e){
            getPlugin().getLogger().severe("Engine not found, hard shutdown");
            onDisable();
        }
    }
}
