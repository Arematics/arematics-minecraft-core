package com.arematics.minecraft.core;

import com.arematics.minecraft.core.listener.BlockWithoutSpringListener;
import com.arematics.minecraft.core.listener.SpringInitializedListener;
import com.arematics.minecraft.core.messaging.injector.LanguageInjector;
import com.arematics.minecraft.core.messaging.injector.StringInjector;
import com.arematics.minecraft.core.server.Server;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.spigotmc.SpigotConfig;
import org.springframework.context.ConfigurableApplicationContext;

@Setter
@Getter
public class CoreBoot extends Bootstrap{

    private final Class<? extends StringInjector> defaultInjectorType = LanguageInjector.class;
    private boolean springInitialized;

    private ConfigurableApplicationContext context;

    /**
     * Hooking Config File
     * Starts the Multi Hook to get all Hooks loaded (Language)
     */
    public CoreBoot() {
        super(true);
        this.springInitialized = false;
    }

    @Override
    public void postEnable() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getPluginManager().registerEvents(new BlockWithoutSpringListener(), this);
        Bukkit.getPluginManager().registerEvents(new SpringInitializedListener(), this);

        SpigotConfig.unknownCommandMessage = this.config.getPrefix() + "§eThat command doesn't exist!";
    }

    @Override
    public void shutdown() {
        Server server = this.getContext().getBean(Server.class);
        server.onStop();
    }
}
