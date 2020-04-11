package com.arematics.minecraft.core.language;

import org.bukkit.entity.Player;

public class LanguageUser {

    private final Player player;
    private Language language;

    public LanguageUser(Player player){
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
