package com.arematics.minecraft.meta.events;

import com.arematics.minecraft.core.events.BaseEvent;
import com.arematics.minecraft.core.items.CoreItem;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class PrepareAnvilEvent extends BaseEvent implements Cancellable {
    private final AnvilInventory anvil;
    private final CoreItem item;
    private final Player enchanter;
    private boolean cancelled;

    public PrepareAnvilEvent(AnvilInventory anvil, ItemStack item, Player enchanter){
        this.anvil = anvil;
        this.item = CoreItem.create(item);
        this.enchanter = enchanter;
    }
}
