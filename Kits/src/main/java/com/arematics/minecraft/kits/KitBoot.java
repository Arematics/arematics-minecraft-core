package com.arematics.minecraft.kits;

import com.arematics.minecraft.core.Bootstrap;
import com.arematics.minecraft.kits.listener.SpringInit;
import org.bukkit.Bukkit;

public class KitBoot extends Bootstrap {

    public KitBoot() {
        super(false);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Bukkit.getPluginManager().registerEvents(new SpringInit(), this);
    }

    @Override
    public void shutdown() {
    }
}
