package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CompoundClassLoader;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.bukkit.Tablist;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.events.SpringInitializedEvent;
import com.arematics.minecraft.core.hooks.PermissionCreationHook;
import com.arematics.minecraft.core.hooks.ScanEnvironment;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.OnlineTimeHandler;
import com.arematics.minecraft.core.server.entities.player.PlayerHandler;
import com.arematics.minecraft.data.service.BroadcastService;
import com.arematics.minecraft.data.share.model.BroadcastMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SpringInitializedListener implements Listener {

    @EventHandler
    public void springInit(SpringInitializedEvent event){
        CoreBoot boot = Boots.getBoot(CoreBoot.class);
        boot.setSpringInitialized(true);
        boot.nextShutdownHandler();
        Server server = Boots.getBoot(CoreBoot.class).getContext().getBean(Server.class);
        server.schedule().runAsync(() -> scanPermissions(boot));
        Reflections reflections = new Reflections(ScanEnvironment.getBuilder("com.arematics.minecraft", (CompoundClassLoader)
                event.getContext().getClassLoader()));
        Set<Class<? extends PlayerHandler>> classes = reflections.getSubTypesOf(PlayerHandler.class);
        classes.forEach(classType -> server.players().registerHandler(classType));
        registerBukkitWorkers(boot);
        Tablist tablist = boot.getContext().getBean(Tablist.class);
        createAsyncTasks(server, tablist);
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
                .forEach((s, listener) -> {
                    boot.getLogger().config("Register Listener: " + listener.getClass().getSimpleName());
                    Bukkit.getPluginManager().registerEvents(listener, boot);
                });
        boot.getContext().getBeansOfType(CoreCommand.class).forEach((s, cmd) -> {
            boot.getLogger().config("Register Command: " + cmd.getClass().getSimpleName());
            cmd.register();
        });
    }

    private void createAsyncTasks(Server server, Tablist tablist){
        server.schedule().asyncRepeat(() -> this.callTwoMinuteAsyncTasks(server), 2, 2, TimeUnit.MINUTES);
        server.schedule().asyncRepeat(() -> callFiveMinuteAsyncTasks(server, tablist), 5, 5, TimeUnit.MINUTES);
    }

    private void callTwoMinuteAsyncTasks(Server server){
        this.saveInventories(server);
    }

    private void callFiveMinuteAsyncTasks(Server server, Tablist tablist){
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
        Bukkit.getOnlinePlayers().stream().map(server.players()::fetchPlayer).forEach(player ->
                player.handle(OnlineTimeHandler.class).updateOnlineTime());
    }

    private void saveInventories(Server server){
        PlayerLeaveSaveListener playerLeaveSaveListener = Boots.getBoot(CoreBoot.class).getContext()
                .getBean(PlayerLeaveSaveListener.class);
        server.getOnline().forEach(player -> server.schedule().runAsync(() ->
                playerLeaveSaveListener.savePlayerData(player.getPlayer(), false)));
    }
}
