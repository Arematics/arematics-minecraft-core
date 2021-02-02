package com.arematics.minecraft.enchants;

import com.arematics.minecraft.core.Bootstrap;
import com.arematics.minecraft.enchants.listener.PullCustomEnchantmentsListener;
import org.bukkit.Bukkit;

public class EnchantBoot extends Bootstrap {

    public EnchantBoot(){
        super(false);
    }

    @Override
    public void postEnable() {
        Bukkit.getPluginManager().registerEvents(new PullCustomEnchantmentsListener(), this);
    }

    @Override
    public void shutdown() {
    }
}
