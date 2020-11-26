package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CompoundClassLoader;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.events.SpringInitializedEvent;
import com.arematics.minecraft.core.hooks.PermissionCreationHook;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.service.InventoryService;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SpringInitializedListener implements Listener {

    @EventHandler
    public void springInit(SpringInitializedEvent event){
        CoreBoot boot = Boots.getBoot(CoreBoot.class);
        boot.setSpringInitialized(true);
        List<ClassLoader> classLoaders = Boots.getBoots().keySet()
                .stream()
                .map(Class::getClassLoader)
                .collect(Collectors.toList());
        CompoundClassLoader loader = new CompoundClassLoader(classLoaders);
        PermissionCreationHook hook = new PermissionCreationHook();
        hook.startPathHook("com.arematics.minecraft", loader, boot);
        boot.getContext().getBeansOfType(Listener.class)
                .forEach((s, listener) -> Bukkit.getPluginManager().registerEvents(listener, boot));
        boot.getContext().getBeansOfType(CoreCommand.class).forEach((s, cmd) -> cmd.register());
        ArematicsExecutor.asyncRepeat(SpringInitializedListener::saveInventories, 2, 2, TimeUnit.MINUTES);
        ArematicsExecutor.asyncRepeat(SpringInitializedListener::updateTimings, 5, 5, TimeUnit.MINUTES);
    }

    private static void updateTimings(){
        Bukkit.getOnlinePlayers().stream().map(CorePlayer::get).forEach(CorePlayer::patchOnlineTime);
    }

    private static void saveInventories(){
        InventoryService service = Boots.getBoot(CoreBoot.class).getContext().getBean(InventoryService.class);
        service.getInventories().keySet().forEach(service::save);
    }
}
