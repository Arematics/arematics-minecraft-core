package com.arematics.minecraft.core.hooks;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public interface Hook<T> {

    void startHook(ClassLoader loader, JavaPlugin plugin);

    Set<T> startPreProcessor(ClassLoader loader, JavaPlugin plugin);

    void processAction(T t, JavaPlugin plugin);
}
