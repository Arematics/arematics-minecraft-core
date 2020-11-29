package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RedstoneLagFixListener implements Listener {

    private static final List<Material> DOOR_TYPES = Arrays.asList(
            Material.ACACIA_DOOR,
            Material.BIRCH_DOOR,
            Material.DARK_OAK_DOOR,
            Material.IRON_DOOR,
            Material.JUNGLE_DOOR,
            Material.SPRUCE_DOOR,
            Material.TRAP_DOOR,
            Material.WOODEN_DOOR
    );
    private static final List<Material> BLACKLISTED_CHUNK_AFFECTED_REDSTONE_BLOCKS = Arrays.asList(
            Material.DIODE_BLOCK_OFF,
            Material.DIODE_BLOCK_ON,
            Material.REDSTONE_TORCH_OFF,
            Material.REDSTONE_TORCH_ON,
            Material.REDSTONE_WIRE
    );

    private static final int BLACKLIST_THRESHOLD = 5; // Prevent any doors if those doors exceeded last tick
    private static final int BLACKLIST_DURATION = 3 * 1000; // How long to prevent in ms

    private final HashMap<Chunk, Long/*ExpireAt*/> blacklistedChunks = new HashMap<>();
    private final HashMap<Chunk, Integer> doorRedstoneTickCounts = new HashMap<>();

    public RedstoneLagFixListener() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Boots.getBoot(CoreBoot.class), () -> {
            // Check if there are chunks, that need to get blocked
            doorRedstoneTickCounts.forEach((chunk, count) -> {
                if (count > BLACKLIST_THRESHOLD) {
                    if (!blacklistedChunks.containsKey(chunk)) {
                        // Play sound to notify any players there, that it's intended to break their redstone.

                        List<Player> playersInChunk = Bukkit.getOnlinePlayers().stream()
                                .filter((p) -> p.getLocation().getChunk() == chunk)
                                .collect(Collectors.toList());
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Boots.getBoot(CoreBoot.class), () -> {
                            for (Player p : playersInChunk)
                                p.playSound(p.getEyeLocation().add(p.getEyeLocation().getDirection()), Sound.ITEM_BREAK, 3, 1f);
                        }, 15);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Boots.getBoot(CoreBoot.class), () -> {
                            for (Player p : playersInChunk)
                                p.playSound(p.getEyeLocation().add(p.getEyeLocation().getDirection()), Sound.ITEM_BREAK, 3, 1.25f);
                        }, 30);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Boots.getBoot(CoreBoot.class), () -> {
                            for (Player p : playersInChunk)
                                p.playSound(p.getEyeLocation().add(p.getEyeLocation().getDirection()), Sound.EAT, 3, 1.5f);
                        }, 40);
                    }
                    blacklistedChunks.put(chunk, System.currentTimeMillis() + BLACKLIST_DURATION);
                }
            });
            doorRedstoneTickCounts.clear();
        }, 1, 1);


        Bukkit.getScheduler().scheduleSyncRepeatingTask(Boots.getBoot(CoreBoot.class), () -> {
            // Find expired chunks
            ArrayList<Chunk> expiredChunks = new ArrayList<>();
            blacklistedChunks.forEach((chunk, expireAt) -> {
                if (expireAt < System.currentTimeMillis())
                    expiredChunks.add(chunk);
            });

            // Remove those expire chunks
            for (Chunk chunk : expiredChunks) blacklistedChunks.remove(chunk);
        }, 20, 20);
    }

    @EventHandler
    public void onRedstoneEvent(BlockRedstoneEvent e) {
        if (e.getNewCurrent() == e.getOldCurrent()) // Actually changed?
            return;
        boolean isDoor = DOOR_TYPES.contains(e.getBlock().getType());
        Chunk chunk = e.getBlock().getChunk();

        if (blacklistedChunks.containsKey(chunk) && (isDoor || BLACKLISTED_CHUNK_AFFECTED_REDSTONE_BLOCKS.contains(e.getBlock().getType()))) {
            e.setNewCurrent(e.getOldCurrent()); // Basically cancel redstone event
            return;
        }

        if (!isDoor)
            return;

        // Add to count
        int count = doorRedstoneTickCounts.getOrDefault(chunk, 0) + 1;
        doorRedstoneTickCounts.put(chunk, count);
        // Max per chunk (instantly enforced)
        int BLOCK_THRESHOLD = 5;
        if (count > BLOCK_THRESHOLD)
            e.setNewCurrent(e.getOldCurrent()); // Basically cancel redstone event
    }
}
