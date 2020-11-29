package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CompoundClassLoader;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.chat.ChatAPI;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.events.SpringInitializedEvent;
import com.arematics.minecraft.core.hooks.PermissionCreationHook;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.core.tablist.Tablist;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.service.BroadcastService;
import com.arematics.minecraft.data.service.InventoryService;
import com.arematics.minecraft.data.share.model.BroadcastMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
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
        ArematicsExecutor.runAsync(() -> scanPermissions(boot));
        registerBukkitWorkers(boot);
        boot.getContext().getBean(ChatAPI.class).bootstrap();
        Tablist tablist = boot.getContext().getBean(Tablist.class);
        createAsyncTasks(tablist);
        tablist.flushOnlines();
    }

    private void scanPermissions(CoreBoot boot){
        List<ClassLoader> classLoaders = Boots.getBoots().keySet()
                .stream()
                .map(Class::getClassLoader)
                .collect(Collectors.toList());
        CompoundClassLoader loader = new CompoundClassLoader(classLoaders);
        PermissionCreationHook hook = new PermissionCreationHook();
        hook.startPathHook("com.arematics.minecraft", loader, boot);
    }

    private void registerBukkitWorkers(CoreBoot boot){
        boot.getContext().getBeansOfType(Listener.class)
                .forEach((s, listener) -> Bukkit.getPluginManager().registerEvents(listener, boot));
        boot.getContext().getBeansOfType(CoreCommand.class).forEach((s, cmd) -> cmd.register());
    }

    private void createAsyncTasks(Tablist tablist){
        ArematicsExecutor.asyncRepeat(this::callTwoMinuteAsyncTasks, 2, 2, TimeUnit.MINUTES);
        ArematicsExecutor.asyncRepeat(() -> callFiveMinuteAsyncTasks(tablist), 5, 5, TimeUnit.MINUTES);
    }

    private void callTwoMinuteAsyncTasks(){
        this.saveInventories();
    }

    private void callFiveMinuteAsyncTasks(Tablist tablist){
        try{
            BroadcastService service = Boots.getBoot(CoreBoot.class).getContext().getBean(BroadcastService.class);
            BroadcastMessage message = service.fetchRandom();
            Messages.create(message.getMessageKey())
                    .to(Bukkit.getOnlinePlayers().toArray(new CommandSender[]{}))
                    .DEFAULT()
                    .replace("prefix", "§c§lInfo » §7")
                    .disableServerPrefix()
                    .handle();
        }catch (RuntimeException ignored){}
        tablist.refreshTeams();
        Bukkit.getOnlinePlayers().stream().map(CorePlayer::get).forEach(CorePlayer::patchOnlineTime);
    }

    private void saveInventories(){
        InventoryService service = Boots.getBoot(CoreBoot.class).getContext().getBean(InventoryService.class);
        service.getInventories().keySet().forEach(service::save);
    }
}
