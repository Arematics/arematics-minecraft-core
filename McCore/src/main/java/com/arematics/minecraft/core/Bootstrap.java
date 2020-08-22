package com.arematics.minecraft.core;

import com.arematics.minecraft.core.annotations.PluginEngine;
import com.arematics.minecraft.core.configurations.Config;
import com.arematics.minecraft.core.hooks.*;
import com.arematics.minecraft.core.utils.ClassUtils;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Bootstrap extends JavaPlugin {

    private Config config;
    private final boolean configuration;

    private String dir = null;

    public Bootstrap(boolean configuration) {
        this.configuration = configuration;
    }
    @Override
    public void onEnable() {
        System.setProperty("file.encoding", "UTF-8");
        this.getLogger().info("Bootstrap enabled, starting Engine!");
        try{
            Boots.addBoot(this);
            this.hook();
            this.config = configuration ? ConfigHook.loadConfig(this) : null;
        }catch (Exception e){
            this.getLogger().severe("Engine startup failed. Stopping Plugin");
            e.printStackTrace();
            onDisable();
        }
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Bootstrap Shutdown called, stopping Engine!");
        try{
            Boots.shutdownBoot(this.getClass());
        }catch (Exception e){
            this.getLogger().severe("Engine not found, hard shutdown");
            e.printStackTrace();
        }
    }

    public String getDir(){
        return dir;
    }

    protected final void hook() throws Exception{
        String dir = ClassUtils.fetchAnnotationValueSave(this, PluginEngine.class, PluginEngine::dir)
                .orElse(getDir());
        if(dir == null) throw new Exception("Engine Package not set in Method getDir or PluginEngine Annotation");
        else this.dir = dir;
        MultiHook hook = new MultiHook(dir, this.getClass().getClassLoader(), this);
        hook.addPreHook(new PreFileExistHook());
        hook.addHook(new LanguageHook());
        hook.addPackageHook(new CommandHooks(), new ListenerHook());
        hook.enable();
    }

    public abstract void shutdown();

    public Config getPluginConfig() {
        return config;
    }
}
