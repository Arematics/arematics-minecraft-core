package com.arematics.minecraft.pvp;

import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.injector.StringInjector;
import com.arematics.minecraft.core.server.Server;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Minecart;
import org.bukkit.scheduler.BukkitRunnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Data
@Component
public class Clearlag {
    LocalDateTime nextExecute;
    private final Server server;

    @Autowired
    public Clearlag(Server server){
        this.server = server;
        start();
    }

    public void start(){
        Duration duration = getDuration();
        nextExecute = LocalDateTime.now().plus(duration);


        if (duration.toMillis() > 120_000) {
            long timeTillFirstExecute = duration.toMillis() - 59_999;
            server.schedule().asyncDelayed(this::mentionClearLag, timeTillFirstExecute, TimeUnit.MILLISECONDS);
        }

        server.schedule().asyncDelayed(this::executeClearLag, duration.toMillis() - 5_000, TimeUnit.MILLISECONDS);
    }

    private void mentionClearLag(){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
        server.getOnline().forEach(player -> player.info("clear_lag_time")
                .DEFAULT()
                .replace("time", dateTimeFormatter.format(player.parseTime(nextExecute)))
                .handle());
    }

    private void executeClearLag(){
        server.schedule().syncRepeat(this::clearLag, 0, 1, TimeUnit.SECONDS, 5);
    }

    private void clearLag(BukkitRunnable runnable, int time){
        float value = ((float) time) / ((float) 5);
        if (time == 0) {
            StringInjector stringInjector = Messages.create("clear_lag_now")
                    .to(Bukkit.getOnlinePlayers().toArray(new CommandSender[]{}))
                    .DEFAULT()
                    .disableServerPrefix();
            server.getOnline().forEach(player -> {
                player.bossBar().set(stringInjector.toObject(player.player()), 1);
                server.schedule().syncDelayed(() -> player.bossBar().hide(), 2, TimeUnit.SECONDS);
            });
            clear();
            server.schedule().asyncDelayed(this::start, 5, TimeUnit.SECONDS);
        } else {
            StringInjector stringInjector = Messages.create("clear_lag_in")
                    .to(Bukkit.getOnlinePlayers().toArray(new CommandSender[]{}))
                    .DEFAULT()
                    .disableServerPrefix()
                    .replace("seconds", String.valueOf(time));
            server.getOnline().forEach(player -> player.bossBar().set(stringInjector.toObject(player.player()), value));
        }
    }

    private void clear(){
        Bukkit.getWorlds().stream().map(World::getEntities).forEach(entities -> entities.forEach(this::clearEntity));
    }

    private void clearEntity(Entity entity){
        if(entity instanceof Item || entity instanceof Minecart){
            entity.remove();
        }
    }

    private Duration getDuration(){
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime nextClear = now.truncatedTo(ChronoUnit.HOURS)
                .plusMinutes(15 * (now.getMinute() / 15))
                .plusMinutes(15);

        return Duration.between(now, nextClear);
    }
}
