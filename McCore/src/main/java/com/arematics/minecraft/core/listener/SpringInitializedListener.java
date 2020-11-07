package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.events.SpringInitializedEvent;
import com.arematics.minecraft.core.hooks.MultiHook;
import com.arematics.minecraft.core.hooks.PermissionCreationHook;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.service.InventoryService;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.concurrent.TimeUnit;

public class SpringInitializedListener implements Listener {

    @EventHandler
    public void springInit(SpringInitializedEvent event){
        CoreBoot boot = Boots.getBoot(CoreBoot.class);
        boot.setSpringInitialized(true);
        MultiHook hook = new MultiHook(boot.getDir(), CoreBoot.class.getClassLoader(), boot);
        hook.addPackageHook(new PermissionCreationHook());
        hook.enable();
        boot.getContext().getBeansOfType(Listener.class)
                .forEach((s, listener) -> Bukkit.getPluginManager().registerEvents(listener, boot));
        boot.getContext().getBeansOfType(CoreCommand.class).forEach((s, cmd) -> cmd.register());
        ArematicsExecutor.asyncRepeat(SpringInitializedListener::saveInventories, 0, 2, TimeUnit.MINUTES);
    }

    private static void saveInventories(){
        InventoryService service = Boots.getBoot(CoreBoot.class).getContext().getBean(InventoryService.class);
        service.getInventories().keySet().forEach(service::save);
    }
}
