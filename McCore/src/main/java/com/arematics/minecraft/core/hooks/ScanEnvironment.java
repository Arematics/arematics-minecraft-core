package com.arematics.minecraft.core.hooks;

import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

public class ScanEnvironment {

    private final Reflections reflections;
    private final JavaPlugin plugin;

    public ScanEnvironment(String url, JavaPlugin plugin){
        this.reflections = new Reflections(url, new TypeAnnotationsScanner(), new SubTypesScanner(),
                new MethodAnnotationsScanner());
        this.plugin = plugin;
    }

    public Reflections getReflections() {
        return reflections;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public Set<Class<?>> getScannedClasses(Class<? extends Annotation> found){
        return this.reflections.getTypesAnnotatedWith(found);
    }

    public Set<Method> getScannedMethods(Class<? extends Annotation> found){
        return this.reflections.getMethodsAnnotatedWith(found);
    }
}
