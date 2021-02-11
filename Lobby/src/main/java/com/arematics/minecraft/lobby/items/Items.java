package com.arematics.minecraft.lobby.items;

import com.arematics.minecraft.core.items.CoreItem;
import org.bukkit.Material;

public class Items {

    public static CoreItem COMPASS;

    static{
        COMPASS = CoreItem.generate(Material.COMPASS).bindCommand("servermenu")
                .setName("§bSelect Server");
    }
}
