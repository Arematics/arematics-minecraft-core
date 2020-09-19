package com.arematics.minecraft.core.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.context.ApplicationContext;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
public class SpringInitializedEvent extends BaseEvent {
    private ApplicationContext context;
    private JavaPlugin plugin;
}
