package com.arematics.minecraft.core;

import com.arematics.minecraft.core.command.processor.parser.Parser;
import com.arematics.minecraft.core.messaging.injector.Injector;
import com.arematics.minecraft.core.messaging.injector.LanguageInjector;
import com.arematics.minecraft.core.messaging.injector.StringInjector;
import lombok.SneakyThrows;
import org.springframework.context.ConfigurableApplicationContext;

public class CoreBoot extends Bootstrap{

    private final Parser parser = new Parser();
    private final Class<? extends StringInjector> defaultInjectorType = LanguageInjector.class;

    private ConfigurableApplicationContext context;
    /**
     * Hooking Config File
     * Starts the Multi Hook to get all Hooks loaded (Language, Commands, Listener)
     */
    public CoreBoot() {
        super(true);
    }

    public Parser getParser() {
        return parser;
    }

    public Class<? extends Injector<?>> getDefaultInjectorType() {
        return defaultInjectorType;
    }

    public ConfigurableApplicationContext getContext() {
        return context;
    }

    public void setContext(ConfigurableApplicationContext context) {
        this.context = context;
    }

    @SneakyThrows
    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void shutdown() {

    }
}
