package com.arematics.minecraft.meta.events;

import com.arematics.minecraft.core.events.BaseEvent;
import com.arematics.minecraft.core.items.CoreItem;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

/**
 * Gets called everytime armor is losing durability or tools losing durability.
 */
@Getter
@Setter
public class DurabilityLoseEvent extends BaseEvent implements Cancellable {
    private final CoreItem item;
    private final int loseAmount;
    private boolean cancelled;

    public DurabilityLoseEvent(ItemStack item, int loseAmount){
        this.item = CoreItem.create(item);
        this.loseAmount = loseAmount;
    }
}
