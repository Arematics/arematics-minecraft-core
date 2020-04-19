package com.arematics.minecraft.core.configurations;

import lombok.Data;
import org.bukkit.Sound;

@Data
public class MessageHighlight {

    private final String colorCode;
    private final Sound sound;
}
