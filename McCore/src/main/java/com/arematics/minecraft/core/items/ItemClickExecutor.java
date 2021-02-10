package com.arematics.minecraft.core.items;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Class providing executing method for specific meta key used by spring boot to hook all meta click executor
 */
@Data
@RequiredArgsConstructor
public abstract class ItemClickExecutor {
    private final String metaKey;
    public abstract boolean execute(CorePlayer clicked, CoreItem item);

    protected String getMetaValue(CoreItem coreItem){
        return coreItem.getMeta().getString(this.getMetaKey());
    }
}
