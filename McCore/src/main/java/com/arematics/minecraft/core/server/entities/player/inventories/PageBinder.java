package com.arematics.minecraft.core.server.entities.player.inventories;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.inventories.helper.IntegerBox;
import com.arematics.minecraft.data.global.model.BukkitItemMapper;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.function.Function;
import java.util.function.Supplier;

@Data
public class PageBinder<T> {

    public static <T> PageBinder<T> of(Supplier<Page<T>> fetch, IntegerBox box, Function<T, CoreItem> mapper){
        return new PageBinder<>(box, fetch, mapper);
    }

    public static <T extends BukkitItemMapper> PageBinder<T> of(Supplier<Page<T>> fetch, IntegerBox box){
        return new PageBinder<>(box, fetch, T::mapToItem);
    }

    private final IntegerBox boxing;
    private final Supplier<Page<T>> fetchPage;
    private final Function<T, CoreItem> mapper;
}
