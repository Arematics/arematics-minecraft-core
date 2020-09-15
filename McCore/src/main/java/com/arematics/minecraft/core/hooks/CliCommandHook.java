package com.arematics.minecraft.core.hooks;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class CliCommandHook extends PackageHook<Class<?>>{

    @Override
    void startPathHook(String url, ClassLoader loader, JavaPlugin plugin) {

    }

    @Override
    public Set<Class<?>> startPreProcessor(ClassLoader loader, JavaPlugin plugin) {
        return null;
    }

    @Override
    public void processAction(Class<?> aClass, JavaPlugin plugin) {

    }
}
