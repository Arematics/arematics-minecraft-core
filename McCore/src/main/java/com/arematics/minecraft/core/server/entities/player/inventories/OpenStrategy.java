package com.arematics.minecraft.core.server.entities.player.inventories;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.Inventories;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.Inventory;

@Getter
@RequiredArgsConstructor
public enum OpenStrategy {
    DEFAULT(false, true),
    TOTAL_BLOCKED(false, false),
    FULL_EDIT(true, true);

    private final boolean editLower;
    private final boolean editUpper;

    public void openInventory(Inventory inventory, CorePlayer player){
        switch(this){
            case TOTAL_BLOCKED: Inventories.openTotalBlockedInventory(inventory, player); return;
            case FULL_EDIT: Inventories.openInventory(inventory, player); return;
            default: Inventories.openLowerDisabledInventory(inventory, player);
        }
    }
}
