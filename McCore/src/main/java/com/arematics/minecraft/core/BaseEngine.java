package com.arematics.minecraft.core;

import com.arematics.minecraft.core.annotations.PluginEngine;
import com.arematics.minecraft.core.configurations.Config;
import com.arematics.minecraft.core.hooks.*;
import com.arematics.minecraft.core.utils.ClassUtils;

public abstract class BaseEngine {

    private final Bootstrap bootstrap;
    private final Config config;

    private String dir = null;

    public BaseEngine(Bootstrap bootstrap, boolean configuration) throws Exception{
        this.bootstrap = bootstrap;
        this.hook();
        if(configuration)
            this.config = ConfigHook.loadConfig(bootstrap);
        else
            this.config = null;
    }

    public String getDir(){
        return dir;
    }

    protected final void hook() throws Exception{
        String dir = ClassUtils.fetchAnnotationValueSave(this, PluginEngine.class, PluginEngine::dir)
                .orElse(getDir());
        if(dir == null) throw new Exception("Engine Package not set in Method getDir or PluginEngine Annotation");
        else this.dir = dir;
        MultiHook hook = new MultiHook(dir, bootstrap.getClass().getClassLoader(), bootstrap);
        hook.addPreHook(new PreFileExistHook());
        hook.addHook(new LanguageHook());
        hook.addPackageHook(new CommandHooks(), new ListenerHook());
        hook.enable();
    }

    public abstract void shutdown();

    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    public Config getConfig() {
        return config;
    }
}
