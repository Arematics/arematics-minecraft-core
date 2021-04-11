package com.arematics.minecraft.core.server.entities.player;

import com.arematics.minecraft.core.utils.ArematicsExecutor;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class TeleportScheduler {

    private final CorePlayer player;
    private final Location location;
    private final boolean instant;

    private Consumer<CorePlayer> execute;

    public TeleportScheduler onEnd(Consumer<CorePlayer> execute){
        this.execute = execute;
        return this;
    }

    public void schedule(){
        if(instant) teleport(0);
        else this.teleport();
    }

    private void execute(){
        player.getPlayer().teleport(location);
        if(this.execute != null) this.execute.accept(this.player);
    }

    private void teleport(){
        if(player.getInTeleport() != null){
            player.warn("Your are in a teleport process right now").handle();
            return;
        }
        ArematicsExecutor.syncRun(() -> location.getChunk().load());
        player.setInTeleport(ArematicsExecutor.asyncRepeat(this::teleport,
                0, 1, TimeUnit.SECONDS, player.hasPermission("world.interact.teleport") ? 0 : 3));
    }

    private void teleport(int count){
        if (count == 0) {
            ArematicsExecutor.syncRun(this::execute);
            player.setInTeleport(null);
        } else
            player.info("%prefix%Teleport in %seconds%§7...").DEFAULT()
                    .replace("prefix", "   §cTP » §7")
                    .replace("seconds", "§c" + count)
                    .disableServerPrefix()
                    .handle();
    }
}
