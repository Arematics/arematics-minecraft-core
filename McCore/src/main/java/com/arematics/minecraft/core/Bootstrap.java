package com.arematics.minecraft.core;

import com.arematics.minecraft.core.configurations.Config;
import com.arematics.minecraft.core.hooks.*;
import org.apache.commons.lang3.ClassUtils;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Bootstrap extends JavaPlugin {

    public Config config;
    public final boolean configuration;

    private final String dir = ClassUtils.getPackageName(this.getClass());

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

    protected final void hook() {
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

    public String getDir() {
        return dir;
    }
}
