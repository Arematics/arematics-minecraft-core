package com.arematics.minecraft.core.language;

import com.arematics.minecraft.core.configurations.MessageHighlight;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

public interface ComponentInject {
    ComponentInject inject(String key, Supplier<String> t);
    void broadcast();
    void send(Player... players);
    ComponentHighlight setMessageHighlight(MessageHighlight highlight);
}
