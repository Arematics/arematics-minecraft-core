package com.arematics.minecraft.core.server.entities.player.inventories.paging;

public interface PagingEasyCLI<T> {
    PagingGUI<T> onCLI(String type, String clickCmd);
}
