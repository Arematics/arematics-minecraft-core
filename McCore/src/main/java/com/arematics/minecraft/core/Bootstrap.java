package com.arematics.minecraft.core;

import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class Bootstrap<T extends BaseEngine> extends JavaPlugin {

    @SuppressWarnings ("unchecked")
    public Class<T> getTypeParameterClass()
    {
        Type type = getClass().getGenericSuperclass();
        ParameterizedType paramType = (ParameterizedType) type;
        return (Class<T>) paramType.getActualTypeArguments()[0];
    }

    private BaseEngine ENGINE;

    @Override
    public void onEnable() {
        System.setProperty("file.encoding", "UTF-8");
        this.getLogger().info("Bootstrap enabled, starting Engine!");
        System.out.println(this.getClass());
        try {
            ENGINE = getTypeParameterClass().getDeclaredConstructor(Bootstrap.class).newInstance(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try{
            Engine.addEngine(ENGINE);
        }catch (Exception e){
            this.getLogger().severe("Engine startup failed. Stopping Plugin");
            e.printStackTrace();
            onDisable();
        }
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Bootstrap Shutdown called, stopping Engine!");
        try{
            Engine.shutdownEngine(ENGINE.getClass());
        }catch (Exception e){
            this.getLogger().severe("Engine not found, hard shutdown");
            e.printStackTrace();
        }
    }
}
