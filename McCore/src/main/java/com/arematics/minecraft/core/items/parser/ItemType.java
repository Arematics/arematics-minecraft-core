package com.arematics.minecraft.core.items.parser;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;

public abstract class ItemType {
    public abstract String propertyValue();
    public abstract Part execute(CorePlayer player, CoreItem item);
}
