package com.arematics.minecraft.core.items;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MetaClickExecutorsCache {

    private final Map<String, ItemClickExecutor> executors = new HashMap<>();

    @Autowired
    public MetaClickExecutorsCache(List<ItemClickExecutor> itemClickExecutorList){
        itemClickExecutorList.forEach(this::addClickExecutor);
    }

    private void addClickExecutor(ItemClickExecutor itemClickExecutor) {
        this.executors.put(itemClickExecutor.getMetaKey(), itemClickExecutor);
    }

    public boolean searchAndRun(CorePlayer clicker, CoreItem clicked){
        return clicked != null && clicked.getMeta().getKeys().stream()
                .filter(this.executors::containsKey)
                .anyMatch(key -> this.executors.get(key).execute(clicker, clicked));
    }
}
