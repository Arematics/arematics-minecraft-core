package com.arematics.minecraft.kits.listener;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.command.processor.parser.CommandParameterParser;
import com.arematics.minecraft.core.events.SpringInitializedEvent;
import com.arematics.minecraft.core.hooks.MultiHook;
import com.arematics.minecraft.core.hooks.PermissionCreationHook;
import com.arematics.minecraft.kits.KitBoot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SpringInit implements Listener {

    @EventHandler
    public void springInit(SpringInitializedEvent event){
        CoreBoot boot = Boots.getBoot(CoreBoot.class);
        boot.setSpringInitialized(true);
        MultiHook hook = new MultiHook(boot.getDir(), KitBoot.class.getClassLoader(), boot);
        hook.addPackageHook(new PermissionCreationHook());
        hook.enable();
        boot.getContext().getBeansOfType(CommandParameterParser.class).keySet().forEach(System.out::println);
    }
}
