package com.arematics.minecraft.core.hooks;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import java.lang.reflect.Method;
import java.util.Set;

public class ListenerHook extends PackageHook<Method> {

    @Override
    void startPathHock(String url, ClassLoader loader, JavaPlugin plugin) {
        try{
            ScanEnvironment.getBuilder().getUrls().clear();
            ScanEnvironment.getBuilder().addUrls(ClasspathHelper.forPackage(url, loader));
            Set<Method> methods = startPreProcessor(loader, plugin);
            methods.forEach(method -> processAction(method, plugin));
        }catch (Exception e){
            plugin.getLogger().warning("Could not find any Listeners");
        }
    }

    @Override
    public Set<Method> startPreProcessor(ClassLoader loader, JavaPlugin plugin) {
        Reflections reflections = new Reflections(ScanEnvironment.getBuilder());
        return reflections.getMethodsAnnotatedWith(EventHandler.class);
    }

    @Override
    public void processAction(Method method, JavaPlugin plugin) {
        Class<?> classprocess = method.getDeclaringClass();
        try {
            Object instance = classprocess.getConstructor().newInstance();
            Bukkit.getLogger().info("Adding " + classprocess.getName() + " as Listener");
            Bukkit.getPluginManager().registerEvents((org.bukkit.event.Listener) instance, plugin);
        }catch (Exception e){
            Bukkit.getLogger().severe("Could not register Listener: " + classprocess.getName());
            e.printStackTrace();
        }
    }
}
