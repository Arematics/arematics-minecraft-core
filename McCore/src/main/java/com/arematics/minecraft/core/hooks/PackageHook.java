package com.arematics.minecraft.core.hooks;

import com.arematics.minecraft.core.annotations.DisableAutoHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

public abstract class PackageHook<T> implements Hook<T> {

    abstract void startPathHook(String url, ClassLoader loader, JavaPlugin plugin);

    @Override
    public void startHook(ClassLoader loader, JavaPlugin plugin) {
        Bukkit.getLogger().severe("Path Hook used");
    }

    public boolean isHookingDisabledFor(Class<?> theClass){
        if(theClass.isAnnotationPresent(DisableAutoHook.class)){
            DisableAutoHook annotation = theClass.getAnnotation(DisableAutoHook.class);
            if(!annotation.whitelist())
                return Arrays.stream(annotation.list()).noneMatch(value -> value == this.getClass());
            else
                return Arrays.stream(annotation.list()).anyMatch(value -> value == this.getClass());
        }

        return false;
    }
}
