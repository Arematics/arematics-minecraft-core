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
            if(methods.isEmpty())
                plugin.getLogger().warning("Could not find any Listeners");
            methods.stream()
                    .map(Method::getDeclaringClass)
                    .distinct()
                    .forEach(methodClass -> processAction(methodClass, plugin));
        }catch (Exception e){
            e.printStackTrace();
            plugin.getLogger().warning("Could not find any Listeners");
        }
    }

    @Override
    public Set<Method> startPreProcessor(ClassLoader loader, JavaPlugin plugin) {
        Reflections reflections = new Reflections(ScanEnvironment.getBuilder());
        return reflections.getMethodsAnnotatedWith(EventHandler.class);
    }

    @Override
    @Deprecated
    public void processAction(Method method, JavaPlugin plugin) {
        throw new RuntimeException("This is only an workaround here");
    }

    public void processAction(Class<?> classprocess, JavaPlugin plugin) {
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
