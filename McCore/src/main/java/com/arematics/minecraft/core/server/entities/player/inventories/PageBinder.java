package com.arematics.minecraft.core.server.entities.player.inventories;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.inventories.helper.IntegerBox;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.function.Function;
import java.util.function.Supplier;

@Data
public class PageBinder<T> {

    public static <T> PageBinder<T> of(Supplier<Page<T>> fetch, IntegerBox box, Function<T, CoreItem> mapper){
        return new PageBinder<>(box, fetch, mapper);
    }

    private final IntegerBox boxing;
    private final Supplier<Page<T>> fetchPage;
    private final Function<T, CoreItem> mapper;
}
