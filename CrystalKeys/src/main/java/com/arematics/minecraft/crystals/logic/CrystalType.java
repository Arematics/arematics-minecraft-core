package com.arematics.minecraft.crystals.logic;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.CorePlayer;

public abstract class CrystalType {
    public abstract String propertyValue();
    public abstract void execute(CorePlayer player, CoreItem item);
}
