package com.arematics.minecraft.core.utils;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ArematicsExecuter {

    public static void runAsync(Runnable runnable){
        CompletableFuture.runAsync(runnable);
    }

    public static String awaitResult(BiConsumer<AtomicReference<String>, CountDownLatch> consumer) throws InterruptedException {
        AtomicReference<String> res = new AtomicReference<>("");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        consumer.accept(res, countDownLatch);
        countDownLatch.await();
        return res.get();
    }

    public static void asyncDelayed(Runnable runnable, long time, TimeUnit unit){
        new BukkitRunnable(){
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLaterAsynchronously(Boots.getBoot(CoreBoot.class), TimeUtils.toTicks(time, unit));
    }

    public static void syncRepeat(Runnable runnable, long delay, long period, TimeUnit unit){
        new BukkitRunnable(){
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskTimer(Boots.getBoot(CoreBoot.class), TimeUtils.toTicks(delay, unit), TimeUtils.toTicks(period, unit));
    }

    public static void syncRepeat(Consumer<Integer> run, long delay, long period, TimeUnit unit, int times){
        new BukkitRunnable(){
            int amount = times;
            @Override
            public void run() {
                run.accept(amount);
                amount--;
                if(amount < 0) this.cancel();
            }
        }.runTaskTimer(Boots.getBoot(CoreBoot.class), TimeUtils.toTicks(delay, unit), TimeUtils.toTicks(period, unit));
    }

    public static void asyncRepeat(Runnable runnable, long delay, long period, TimeUnit unit){
        new BukkitRunnable(){
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskTimerAsynchronously(Boots.getBoot(CoreBoot.class), TimeUtils.toTicks(delay, unit),
                TimeUtils.toTicks(period, unit));
    }

    public static void asyncRepeat(Consumer<Integer> run, long delay, long period, TimeUnit unit, int times){
        new BukkitRunnable(){
            int amount = times;
            @Override
            public void run() {
                run.accept(amount);
                amount--;
                if(amount < times) this.cancel();
            }
        }.runTaskTimerAsynchronously(Boots.getBoot(CoreBoot.class), TimeUtils.toTicks(delay, unit),
                TimeUtils.toTicks(period, unit));
    }
}
