package com.arematics.minecraft.spring;

import org.bukkit.configuration.file.FileConfiguration;
import org.springframework.core.env.PropertySource;

public class ConfigurationPropertySource extends PropertySource<FileConfiguration> {

    ConfigurationPropertySource(FileConfiguration source) {
        super("config", source);
    }

    @Override
    public Object getProperty(String s) {
        return source.get(s);
    }
}
