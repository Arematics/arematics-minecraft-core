package com.arematics.minecraft.core;

import com.arematics.minecraft.core.configurations.Config;
import com.arematics.minecraft.core.hooks.ConfigHook;
import com.arematics.minecraft.core.hooks.LanguageHook;
import com.arematics.minecraft.core.hooks.MultiHook;
import com.arematics.minecraft.core.hooks.PreFileExistHook;
import org.apache.commons.lang3.ClassUtils;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Bootstrap extends JavaPlugin {

    public Config config;
    public final boolean configuration;

    protected final MultiHook hook;

    public Bootstrap(boolean configuration) {
        this.configuration = configuration;
        String dir = ClassUtils.getPackageName(this.getClass());
        this.hook = new MultiHook(dir, this.getClass().getClassLoader(), this);
    }

    @Override
    public final void onEnable() {
        System.setProperty("file.encoding", "UTF-8");
        this.getLogger().info("Bootstrap enabled, starting Engine!");
        try{
            Boots.addBoot(this);
            this.hook();
            this.config = configuration ? ConfigHook.loadConfig(this) : null;
            this.postEnable();
        }catch (Exception e){
            this.getLogger().severe("Engine startup failed. Stopping Plugin");
            e.printStackTrace();
            onDisable();
        }
    }

    public void postEnable() {}

    @Override
    public final void onDisable() {
        this.getLogger().info("Bootstrap Shutdown called, stopping Engine!");
        try{
            Boots.shutdownBoot(this.getClass());
            this.postDisable();
        }catch (Exception e){
            this.getLogger().severe("Engine not found, hard shutdown");
            e.printStackTrace();
        }
    }

    public void postDisable() {}

    protected final void hook() {
        hook.addPreHook(new PreFileExistHook());
        hook.addHook(new LanguageHook());
        hook.enable();
    }

    public abstract void shutdown();

    public Config getPluginConfig() {
        return config;
    }

    public String getDir() {
        return dir;
    }

    public void callEvent(Event event){
        this.getServer().getPluginManager().callEvent(event);
    }
}
