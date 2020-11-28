package com.arematics.minecraft.animations.firework;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.security.SecureRandom;
import java.util.List;

public class FireworkBuilder {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static FireworkBuilder create(){
        return new FireworkBuilder();
    }

    private boolean flicker;
    private boolean trail;
    private FireworkEffect.Type type;
    private int power;
    private List<Color> colors;

    private FireworkBuilder(){}

    public FireworkBuilder setFlicker(boolean flicker){
        this.flicker = flicker;
        return this;
    }

    public FireworkBuilder setTrail(boolean trail){
        this.trail = trail;
        return this;
    }

    public FireworkBuilder setColors(List<Color> colors){
        this.colors = colors;
        return this;
    }

    public FireworkBuilder setType(FireworkEffect.Type type){
        this.type = type;
        return this;
    }

    public FireworkBuilder setPower(int power){
        this.power = power;
        return this;
    }

    public void spawn(Player player, Location loc){
    }

}
