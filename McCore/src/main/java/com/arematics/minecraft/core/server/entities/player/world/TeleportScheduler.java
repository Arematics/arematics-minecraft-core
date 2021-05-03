package com.arematics.minecraft.core.server.entities.player.world;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TeleportScheduler {

    private static ArematicsExecutor arematicsExecutor;

    private final CorePlayer player;
    private final Location location;
    private final boolean instant;

    private Consumer<CorePlayer> execute;

    public TeleportScheduler(CorePlayer player, Location location, boolean instant){
        if(arematicsExecutor == null)
            arematicsExecutor = Boots.getBoot(CoreBoot.class).getContext().getBean(ArematicsExecutor.class);
        this.player = player;
        this.location = location;
        this.instant = instant;
    }

    public TeleportScheduler onEnd(Consumer<CorePlayer> execute){
        this.execute = execute;
        return this;
    }

    public void schedule(){
        if(instant) teleport(null, 0);
        else this.teleport();
    }

    private void execute(){
        player.getPlayer().teleport(location);
        if(this.execute != null) this.execute.accept(this.player);
    }

    private void teleport(){
        if(player.interact().inTeleport() != null){
            player.warn("Your are in a teleport process right now").handle();
            return;
        }
        player.interact().inTeleport(arematicsExecutor.asyncRepeat(this::teleport,
                0, 1, TimeUnit.SECONDS, player.hasPermission("world.interact.teleport") ? 0 : 3));
    }

    private void teleport(BukkitRunnable runnable, int count){
        if (count == 0) {
            arematicsExecutor.runSync(this::execute);
            player.interact().inTeleport(null);
        } else
            player.info("%prefix%Teleport in %seconds%§7...").DEFAULT()
                    .replace("prefix", "   §cTP » §7")
                    .replace("seconds", "§c" + count)
                    .disableServerPrefix()
                    .handle();
    }
}
