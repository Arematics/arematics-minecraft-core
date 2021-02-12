package com.arematics.minecraft.core;

import com.arematics.minecraft.core.listener.BlockWithoutSpringListener;
import com.arematics.minecraft.core.listener.SpringInitializedListener;
import com.arematics.minecraft.core.messaging.injector.LanguageInjector;
import com.arematics.minecraft.core.messaging.injector.StringInjector;
import com.arematics.minecraft.core.server.Clearlag;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.TimeUnit;

@Setter
@Getter
public class CoreBoot extends Bootstrap{

    private final Class<? extends StringInjector> defaultInjectorType = LanguageInjector.class;
    private boolean springInitialized;
    private final Clearlag clearlag;

    private ConfigurableApplicationContext context;

    /**
     * Hooking Config File
     * Starts the Multi Hook to get all Hooks loaded (Language)
     */
    public CoreBoot() {
        super(true);
        this.springInitialized = false;
        this.clearlag = new Clearlag();
    }

    @Override
    public void postEnable() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getPluginManager().registerEvents(new BlockWithoutSpringListener(), this);
        Bukkit.getPluginManager().registerEvents(new SpringInitializedListener(), this);
        ArematicsExecutor.asyncDelayed(this.clearlag::start, 10, TimeUnit.SECONDS);
    }

    @Override
    public void shutdown() {

    }
}
