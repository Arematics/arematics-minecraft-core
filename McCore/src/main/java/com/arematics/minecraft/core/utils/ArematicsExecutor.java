package com.arematics.minecraft.core.utils;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.anvil.AnvilGUI;
import com.arematics.minecraft.core.server.entities.player.inventories.numbers.NumberGUI;
import com.arematics.minecraft.core.times.TimeUtils;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

@Service
public class ArematicsExecutor implements TaskExecutor {

    public String awaitAnvilResult(String key, CorePlayer player) throws InterruptedException {
        return awaitAnvilResult(key, "", player);
    }

    public String awaitAnvilResult(String key, String startValue, CorePlayer player) throws InterruptedException {
        return awaitResult((res, latch) ->
                new AnvilGUI(Boots.getBoot(CoreBoot.class), player.getPlayer(), key + ": ", startValue, (p, result) -> {
                    res.set(result);
                    latch.countDown();
                    return null;
                }));
    }

    public Number awaitNumberResult(String key, Number min, Number startValue, CorePlayer player)
            throws InterruptedException {
        return awaitNumberResult((res, latch) ->
                new NumberGUI(key, min, startValue, player, (p, result) -> {
                    res.set(result);
                    latch.countDown();
                }));
    }

    public Number awaitNumberResult(String key, Number min, Number max, Number startValue, CorePlayer player)
            throws InterruptedException {
        return awaitNumberResult((res, latch) ->
                new NumberGUI(key, min, max, startValue, true, player, (p, result) -> {
                    res.set(result);
                    latch.countDown();
                }));
    }

    public String awaitResult(BiConsumer<AtomicReference<String>, CountDownLatch> consumer) throws InterruptedException {
        AtomicReference<String> res = new AtomicReference<>("");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        consumer.accept(res, countDownLatch);
        countDownLatch.await();
        return res.get();
    }

    public Number awaitNumberResult(BiConsumer<AtomicReference<Number>, CountDownLatch> consumer) throws InterruptedException {
        AtomicReference<Number> res = new AtomicReference<>(0);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        consumer.accept(res, countDownLatch);
        countDownLatch.await();
        return res.get();
    }

    @Override
    public void runSync(Runnable runnable){
        new BukkitRunnable(){
            @Override
            public void run() {
                runnable.run();
            }
        }.runTask(Boots.getBoot(CoreBoot.class));
    }

    @Override
    public void runAsync(Runnable runnable){
        CompletableFuture.runAsync(runnable);
    }

    @Override
    public BukkitTask syncDelayed(Runnable runnable, long time, TimeUnit unit){
        return new BukkitRunnable(){
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLater(Boots.getBoot(CoreBoot.class), TimeUtils.toTicks(time, unit));
    }

    @Override
    public BukkitTask asyncDelayed(Runnable runnable, long time, TimeUnit unit){
        return new BukkitRunnable(){
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLaterAsynchronously(Boots.getBoot(CoreBoot.class), TimeUtils.toTicks(time, unit));
    }

    @Override
    public BukkitTask syncRepeat(Runnable runnable, long delay, long period, TimeUnit unit){
        delay = TaskExecutor.toTicks(delay, unit);
        period = TaskExecutor.toTicks(period, unit);
        return new BukkitRunnable(){
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskTimer(Boots.getBoot(CoreBoot.class), delay, period);
    }

    @Override
    public BukkitTask asyncRepeat(Runnable runnable, long delay, long period, TimeUnit unit){
        delay = TaskExecutor.toTicks(delay, unit);
        period = TaskExecutor.toTicks(period, unit);
        return new BukkitRunnable(){
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskTimerAsynchronously(Boots.getBoot(CoreBoot.class), delay, period);
    }

    @Override
    public BukkitTask syncRepeat(BiConsumer<BukkitRunnable, Integer> run, long delay, long period, TimeUnit unit, int times){
        delay = TaskExecutor.toTicks(delay, unit);
        period = TaskExecutor.toTicks(period, unit);
        return new BukkitRunnable(){
            int amount = times;
            @Override
            public void run() {
                run.accept(this, amount);
                if(--amount < 0) this.cancel();
            }
        }.runTaskTimer(Boots.getBoot(CoreBoot.class), delay, period);
    }

    @Override
    public BukkitTask asyncRepeat(BiConsumer<BukkitRunnable, Integer> consumer, long delay, long period, TimeUnit unit, int times){
        delay = TaskExecutor.toTicks(delay, unit);
        period = TaskExecutor.toTicks(period, unit);
        return new BukkitRunnable(){
            int amount = times;
            @Override
            public void run() {
                consumer.accept(this, amount);
                if(--amount < 0) this.cancel();
            }
        }.runTaskTimerAsynchronously(Boots.getBoot(CoreBoot.class), delay, period);
    }
}
