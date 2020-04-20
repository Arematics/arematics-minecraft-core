package com.arematics.minecraft.core.hooks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MultiHook {

    private final String url;
    private final ClassLoader loader;
    private final JavaPlugin bootstrap;

    private Set<Hook> preHooks = new HashSet<>();
    private Set<Hook> hooks = new HashSet<>();
    private Set<PackageHook> packageHooks = new HashSet<>();

    public MultiHook(String url, ClassLoader loader, JavaPlugin bootstrap){
        this.url = url;
        this.loader = loader;
        this.bootstrap = bootstrap;
    }

    public String getUrl() {
        return url;
    }

    public JavaPlugin getBootstrap() {
        return bootstrap;
    }

    public ClassLoader getLoader() {
        return loader;
    }

    public void addPreHook(Hook hook){
        this.preHooks.add(hook);
    }

    public void addPreHook(Hook... hook){
        this.preHooks.addAll(Arrays.asList(hook));
    }


    public void addHook(Hook hook){
        this.hooks.add(hook);
    }

    public void addHook(Hook... hook){
        this.hooks.addAll(Arrays.asList(hook));
    }


    public void addPackageHook(PackageHook hook){
        this.packageHooks.add(hook);
    }

    public void addPackageHook(PackageHook... hook){
        this.packageHooks.addAll(Arrays.asList(hook));
    }

    public void enable(){
        bootstrap.getLogger().info("Enable Pre Hooks");
        preHooks.forEach(hook -> hook.startHook(getLoader(), getBootstrap()));
        bootstrap.getLogger().info("Enable Hooks");
        hooks.forEach(hook -> hook.startHook(getLoader(), getBootstrap()));
        packageHooks.forEach(phook -> phook.startPathHock(getUrl(), getLoader(), getBootstrap()));
        bootstrap.getLogger().info("All Hooks enabled");
    }
}
