package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.events.SpringInitializedEvent;
import com.arematics.minecraft.core.hooks.MultiHook;
import com.arematics.minecraft.core.hooks.PermissionCreationHook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SpringInitializedListener implements Listener {

    @EventHandler
    public void springInit(SpringInitializedEvent event){
        CoreBoot boot = Boots.getBoot(CoreBoot.class);
        boot.setSpringInitialized(true);
        MultiHook hook = new MultiHook(boot.getDir(), CoreBoot.class.getClassLoader(), boot);
        hook.addPackageHook(new PermissionCreationHook());
        hook.enable();
    }
}
