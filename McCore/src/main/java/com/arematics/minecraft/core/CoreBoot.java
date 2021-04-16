package com.arematics.minecraft.core;

import com.arematics.minecraft.core.listener.BlockWithoutSpringListener;
import com.arematics.minecraft.core.listener.SpringInitializedListener;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.injector.LanguageInjector;
import com.arematics.minecraft.core.messaging.injector.StringInjector;
import com.arematics.minecraft.core.proxy.MessagingUtils;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.times.TimeUtils;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.comphenix.protocol.ProtocolLibrary;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.spigotmc.SpigotConfig;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Setter
@Getter
public class CoreBoot extends Bootstrap{

    private final Class<? extends StringInjector> defaultInjectorType = LanguageInjector.class;
    private boolean springInitialized;

    private ConfigurableApplicationContext context;

    /**
     * Hooking Config File
     * Starts the Multi Hook to get all Hooks loaded (Language)
     */
    public CoreBoot() {
        super(true);
        this.springInitialized = false;
    }

    @Override
    public void postEnable() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getPluginManager().registerEvents(new BlockWithoutSpringListener(), this);
        Bukkit.getPluginManager().registerEvents(new SpringInitializedListener(), this);

        SpigotConfig.unknownCommandMessage = this.config.getPrefix() + "§eThat command doesn't exist!";
    }

    @Override
    public void shutdown() {
        Server server = this.getContext().getBean(Server.class);
        server.onStop();
        ProtocolLibrary.getProtocolManager().removePacketListeners(Boots.getBoot(CoreBoot.class));
    }

    public void nextShutdownHandler(){
        Server server = getContext().getBean(Server.class);
        Duration nextShutdown = untilNextShutdown();
        LocalDateTime time = LocalDateTime.now().plus(nextShutdown);

        this.getLogger().warning("Next shutdown is at: " + time.toString());

        long timeTillFirstExecute = (nextShutdown.toMillis() / 1000) - 300;
        long timeTillSecondExecute = (nextShutdown.toMillis() / 1000) - 100;
        ArematicsExecutor.asyncDelayed(() ->
                        sendShutdownIn(server, TimeUtils.toString(System.currentTimeMillis() - nextShutdown.toMillis())),
                timeTillFirstExecute, TimeUnit.SECONDS);

        ArematicsExecutor.asyncDelayed(() ->
                        sendShutdownIn(server, TimeUtils.toString(System.currentTimeMillis() - nextShutdown.toMillis())),
                timeTillSecondExecute, TimeUnit.SECONDS);


        ArematicsExecutor.asyncDelayed(() ->
                        startShutdownSchedule(server),
                nextShutdown.toMillis() - 11_000, TimeUnit.MILLISECONDS);
    }

    private void startShutdownSchedule(Server server){
        ArematicsExecutor.syncRepeat((count) -> shutdown(count, server), 0, 1, TimeUnit.SECONDS, 10);
    }

    private void shutdown(int time, Server server){
        if(time != 0){
            Messages.create("Server restart in %seconds%")
                    .WARNING()
                    .to(Bukkit.getOnlinePlayers().toArray(new Player[]{}))
                    .DEFAULT()
                    .replace("seconds", String.valueOf(time))
                    .handle();
        }else{

            Messages.create("Server restart now")
                    .WARNING()
                    .to(Bukkit.getOnlinePlayers().toArray(new Player[]{}))
                    .DEFAULT()
                    .handle();
            this.getLogger().warning("Shutdown executed");
            server.getOnline().forEach(player -> MessagingUtils.sendToServer(player, "lobbyone"));
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "stop");
        }
    }

    private void sendShutdownIn(Server server, String msg){
        server.getOnline().forEach(player -> player.warn("Server restart in " + msg).handle());
    }

    private Duration untilNextShutdown(){
        LocalDateTime now = LocalDateTime.now();
        File file = new File("lobby.is");
        LocalDateTime next = now.withHour(4).withMinute(25).withSecond(0);
        if(file.exists()) next = next.plusSeconds(10);
        if(now.compareTo(next) > 0) next = next.plusDays(1);

        return Duration.between(now, next);
    }
}
