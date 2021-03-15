package com.arematics.minecraft.core.server.entities.player.inventories.paging;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.data.domain.Page;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface PagingGUI<T> {
    PagingEnd onGUI(BiConsumer<CorePlayer, Supplier<Page<T>>> consumer);
}
