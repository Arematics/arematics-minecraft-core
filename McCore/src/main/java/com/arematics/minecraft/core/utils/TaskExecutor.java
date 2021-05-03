package com.arematics.minecraft.core.utils;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public interface TaskExecutor {

    /**
     * Run a task synchronously to the bukkit main thread
     * @param runnable Task to run
     */
    void runSync(Runnable runnable);

    /**
     * Run a task asynchronously to the bukkit main thread
     * @param runnable Task to run
     */
    void runAsync(Runnable runnable);

    /**
     * Run a task synchronously to the bukkit main thread after a specified delay
     * @param runnable Task to run
     * @param time Delay Time before execution
     * @param unit Time Unit
     * @return Task to cancel it if necessary
     */
    BukkitTask syncDelayed(Runnable runnable, long time, TimeUnit unit);

    /**
     * Run a task asynchronously to the bukkit main thread after a specified delay
     * @param runnable Task to run
     * @param time Delay Time before execution
     * @param unit Time Unit
     * @return Task to cancel it if necessary
     */
    BukkitTask asyncDelayed(Runnable runnable, long time, TimeUnit unit);

    /**
     * Run a task synchronously to the bukkit main thread after a specified
     * delay and rerunning it after a specified period again
     * @param runnable Task to run
     * @param delay Delay Time before first start
     * @param period Period Time
     * @param unit Time Unit
     * @return Task to cancel it if necessary
     */
    BukkitTask syncRepeat(Runnable runnable, long delay, long period, TimeUnit unit);

    /**
     * Run a task asynchronously to the bukkit main thread after a specified
     * delay and rerunning it after a specified period again
     * @param runnable Task to run
     * @param delay Delay Time before first start
     * @param period Period Time
     * @param unit Time Unit
     * @return Task to cancel it if necessary
     */
    BukkitTask asyncRepeat(Runnable runnable, long delay, long period, TimeUnit unit);

    /**
     * Run a task synchronously to the bukkit main thread after a specified
     * delay and rerunning it after a specified period again.
     *
     * Times is used to run it specific times ending when counter is 0. Ending must be implemented
     * @param consumer Consumer with current counter value and BukkitRunnable to cancel it if necessary
     * @param delay Delay Time before first start
     * @param period Period Time
     * @param unit Time Unit
     * @param times How many times task should run
     * @return Task to cancel it if necessary
     */
    BukkitTask syncRepeat(BiConsumer<BukkitRunnable, Integer> consumer, long delay, long period, TimeUnit unit, int times);

    /**
     * Run a task asynchronously to the bukkit main thread after a specified
     * delay and rerunning it after a specified period again.
     *
     * Times is used to run it specific times ending when counter is 0. Ending must be implemented
     * @param consumer Consumer with current counter value and BukkitRunnable to cancel it if necessary
     * @param delay Delay Time before first start
     * @param period Period Time
     * @param unit Time Unit
     * @param times How many times task should run
     * @return Task to cancel it if necessary
     */
    BukkitTask asyncRepeat(BiConsumer<BukkitRunnable, Integer> consumer, long delay, long period, TimeUnit unit, int times);

    /**
     * Convert TimeUnit Value to Bukkit Ticks
     *
     * @param time Value
     * @param unit TimeUnit
     * @return Converted TimeUnit Value to Bukkit Ticks
     */
    static long toTicks(long time, TimeUnit unit){
        return unit.toMillis(time) / 50;
    }
}
