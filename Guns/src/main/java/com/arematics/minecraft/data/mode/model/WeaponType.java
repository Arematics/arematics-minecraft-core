package com.arematics.minecraft.data.mode.model;

import com.arematics.minecraft.core.items.CoreItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
@RequiredArgsConstructor
public enum WeaponType {
    MACHINE_GUN(CoreItem.create(new ItemStack(Material.SEEDS))),
    SNIPER(CoreItem.create(new ItemStack(Material.CLAY))),
    SHOTGUN(CoreItem.create(new ItemStack(Material.INK_SACK, 1, (short) 15)));

    private final CoreItem ammunition;
}
