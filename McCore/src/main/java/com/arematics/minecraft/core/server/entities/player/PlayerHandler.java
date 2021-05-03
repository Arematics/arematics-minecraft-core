package com.arematics.minecraft.core.server.entities.player;

public abstract class PlayerHandler {

    protected CorePlayer player;

    public void init(CorePlayer player){
        this.player = player;
    }
}
