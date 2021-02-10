package com.arematics.minecraft.guns.enchants;

import com.arematics.minecraft.enchants.ArematicsEnchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.springframework.stereotype.Component;

@Component
public class ArmorPiercing extends ArematicsEnchantment {

    public ArmorPiercing() {
        super(123, "ArmorPiercing", EnchantmentTarget.TOOL);
    }

    @Override
    public int getMaxLevel() {
        return 0;
    }
}
