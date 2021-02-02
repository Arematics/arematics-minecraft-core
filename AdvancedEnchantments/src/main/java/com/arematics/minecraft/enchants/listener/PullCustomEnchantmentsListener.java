package com.arematics.minecraft.enchants.listener;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.events.SpringInitializedEvent;
import com.arematics.minecraft.enchants.ArematicsEnchantment;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collection;

public class PullCustomEnchantmentsListener implements Listener {

    @EventHandler
    public void init(SpringInitializedEvent event){
        Collection<ArematicsEnchantment> enchantments = Boots.getBoot(CoreBoot.class)
                .getContext().getBeansOfType(ArematicsEnchantment.class).values();

        enchantments.forEach(Enchantment::registerEnchantment);
    }
}
