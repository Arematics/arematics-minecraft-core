package com.arematics.minecraft.core.hooks;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import java.lang.reflect.Method;
import java.util.Set;

public class ListenerHook{

    /**
     * Clearing Urls of Configuration Builder and adds the new url starting Reflection Scan and reads all Methods
     * annotated with {@link org.bukkit.event.EventHandler}
     * @param url Package Path
     * @param loader ClassLoader where hookListeners is called
     * @param plugin JavaPlugin Class where Listeners should be added
     */
    public static void hookListeners(String url, ClassLoader loader, JavaPlugin plugin){
        try{
            ScanEnvironment.getBuilder().getUrls().clear();
            ScanEnvironment.getBuilder().addUrls(ClasspathHelper.forPackage(url, loader));
            Reflections reflections = new Reflections(ScanEnvironment.getBuilder());
            Set<Method> methods = reflections.getMethodsAnnotatedWith(EventHandler.class);
            Bukkit.getLogger().info("Starting Listener Hook");
            methods.forEach(method -> processClass(method, plugin));
        }catch (Exception e){
            e.printStackTrace();
            Bukkit.getLogger().warning("Could not find any Listeners in: " + plugin.getName());
        }
    }

    private static void processClass(Method method, JavaPlugin plugin){
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
