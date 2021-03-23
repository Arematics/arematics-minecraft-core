package com.arematics.minecraft.data.global.model;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.Server;

public interface BukkitItemMapper {
    CoreItem mapToItem(Server server);
}
