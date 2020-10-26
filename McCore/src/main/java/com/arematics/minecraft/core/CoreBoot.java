package com.arematics.minecraft.core;

import com.arematics.minecraft.core.chat.ChatAPI;
import com.arematics.minecraft.core.command.processor.parser.Parser;
import com.arematics.minecraft.core.messaging.injector.Injector;
import com.arematics.minecraft.core.listener.SpringInitializedListener;
import com.arematics.minecraft.core.messaging.injector.LanguageInjector;
import com.arematics.minecraft.core.messaging.injector.StringInjector;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.springframework.context.ConfigurableApplicationContext;

@Setter
@Getter
public class CoreBoot extends Bootstrap{

    private final Class<? extends StringInjector> defaultInjectorType = LanguageInjector.class;
    private boolean springInitialized;

    private ConfigurableApplicationContext context;
    /**
     * Hooking Config File
     * Starts the Multi Hook to get all Hooks loaded (Language, Commands, Listener)
     */
    public CoreBoot() {
        super(true);
        this.springInitialized = false;
    }

    @SneakyThrows
    @Override
    public void onEnable() {
        super.onEnable();
        Bukkit.getPluginManager().registerEvents(new SpringInitializedListener(), this);
    }

    @Override
    public void shutdown() {

    }
}
