package com.arematics.minecraft.crystals.logic;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.data.mode.model.CrystalKey;

public abstract class CrystalType {
    public abstract String propertyValue();
    public abstract void execute(CorePlayer player, CoreItem item, CrystalKey key);
}
