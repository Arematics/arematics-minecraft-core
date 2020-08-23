package com.arematics.minecraft.core.hooks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public abstract class PackageHook<T> implements Hook<T> {

    abstract void startPathHock(String url, ClassLoader loader, JavaPlugin plugin);

    @Override
    public void startHook(ClassLoader loader, JavaPlugin plugin) {
        Bukkit.getLogger().severe("Path Hook used");
    }
}
