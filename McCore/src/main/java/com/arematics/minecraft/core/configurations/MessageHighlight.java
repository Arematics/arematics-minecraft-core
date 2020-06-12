package com.arematics.minecraft.core.configurations;

import org.bukkit.Sound;

public class MessageHighlight {

    private final String colorCode;
    private final Sound sound;

    public MessageHighlight(String colorCode, Sound sound){
        this.colorCode = colorCode;
        this.sound = sound;
    }

    public String getColorCode() {
        return colorCode;
    }

    public Sound getSound() {
        return sound;
    }
}
