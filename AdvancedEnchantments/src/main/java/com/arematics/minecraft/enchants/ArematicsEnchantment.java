package com.arematics.minecraft.enchants;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public abstract class ArematicsEnchantment extends Enchantment {

    private final String name;
    private final EnchantmentTarget target;

    public ArematicsEnchantment(int id, String name, EnchantmentTarget target){
        super(id);
        this.name = name;
        this.target = target;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return this.target;
    }

    @Override
    public boolean conflictsWith(Enchantment enchantment) {
        return enchantment.conflictsWith(this);
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return !itemStack.containsEnchantment(this);
    }
}
