package com.arematics.minecraft.core;

import com.arematics.minecraft.core.annotations.PluginEngine;
import com.arematics.minecraft.core.command.processor.parser.Parser;
import com.arematics.minecraft.core.hooks.ConfigHook;
import com.arematics.minecraft.core.messaging.injector.Injector;
import com.arematics.minecraft.core.messaging.injector.LanguageInjector;
import com.arematics.minecraft.core.messaging.injector.StringInjector;
import com.arematics.minecraft.spring.SpringSpigotBootstrapper;
import lombok.SneakyThrows;
import org.springframework.context.ConfigurableApplicationContext;

@PluginEngine(dir = "com.arematics.minecraft.core")
public class CoreBoot extends Bootstrap{

    private final Parser parser = new Parser();
    private final Class<? extends StringInjector> defaultInjectorType = LanguageInjector.class;

    private ConfigurableApplicationContext context;
    /**
     * Hooking Config File
     * Starts the Multi Hook to get all Hooks loaded (Language, Commands, Listener)
     */
    public CoreBoot() throws Exception{
        super(true);
    }

    public Parser getParser() {
        return parser;
    }

    public Class<? extends Injector<?>> getDefaultInjectorType() {
        return defaultInjectorType;
    }


    @SneakyThrows
    @Override
    public void onEnable() {
        System.setProperty("file.encoding", "UTF-8");
        this.getLogger().info("Bootstrap enabled, starting Engine!");
        try{
            Boots.addBoot(this);
            this.hook();
            this.config = configuration ? ConfigHook.loadConfig(this) : null;
            context = SpringSpigotBootstrapper.initialize(this, Application.class);
        }catch (Exception e){
            this.getLogger().severe("Engine startup failed. Stopping Plugin");
            e.printStackTrace();
            onDisable();
        }
    }

    @Override
    public void shutdown() {

    }
}
