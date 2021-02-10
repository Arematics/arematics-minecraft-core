package com.arematics.minecraft.core;

import lombok.val;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SpringSpigotBootstrapper {


    private SpringSpigotBootstrapper() {
    }

    public static ConfigurableApplicationContext initialize(JavaPlugin plugin, ClassLoader classLoader, Class<?> applicationClass) throws ExecutionException, InterruptedException {
        return initialize(plugin, classLoader, new SpringApplicationBuilder(applicationClass));
    }

    public static ConfigurableApplicationContext initialize(JavaPlugin plugin, ClassLoader classLoader, SpringApplicationBuilder builder) throws ExecutionException, InterruptedException {
        val executor = Executors.newSingleThreadExecutor();
        try {
            Future<ConfigurableApplicationContext> contextFuture = executor.submit(() -> {
                Thread.currentThread().setContextClassLoader(classLoader);

                val props = new Properties();
                try {
                    File file = new File(plugin.getDataFolder() + "/spring.properties");
                    props.load(new FileInputStream(file));
                } catch (Exception ignored) {
                }

                if (builder.application().getResourceLoader() == null) {
                    val loader = new DefaultResourceLoader(classLoader);
                    builder.resourceLoader(loader);
                }

                return builder
                        .properties(props)
                        .initializers(new SpringSpigotInitializer(plugin))
                        .run();
            });
            return contextFuture.get();
        } finally {
            executor.shutdown();
        }
    }
}
