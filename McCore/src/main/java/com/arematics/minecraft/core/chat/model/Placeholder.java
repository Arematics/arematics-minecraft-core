package com.arematics.minecraft.core.chat.model;

import org.bukkit.entity.Player;

import java.util.Map;

public interface Placeholder {
    String getPlaceholderName();
    String getPlaceholderMatch();
    Map<Player, String> getPlaceholderValues();
}
