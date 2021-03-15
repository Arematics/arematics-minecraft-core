package com.arematics.minecraft.core.server.entities.player.inventories.paging;

import com.arematics.minecraft.core.messaging.advanced.MSG;

import java.util.function.Function;

public interface PagingCLI<T> {
    PagingGUI<T> onCLI(Function<T, MSG> messageMapper, String type, String clickCmd);
}
