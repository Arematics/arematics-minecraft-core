package com.arematics.minecraft.core.pages;

import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Pageable {

    private final static int maxItems = 36;

    private final Map<Long, Page> pages = new HashMap<>();
    private final CommandSender sender;
    private final String bindedCommand;
    private final int maxCacheSeconds;
    private long page = 0;
    private long lastUse;

    public Pageable(List<String> content, CommandSender sender, String bindedCommand, int maxCacheSeconds){
        this.sender = sender;
        this.bindedCommand = bindedCommand;
        this.maxCacheSeconds = maxCacheSeconds;
        generate(content);
    }

    private void generate(List<String> content){
        this.pages.clear();
        List<List<String>> partitions = Lists.partition(content, maxItems);
        IntStream.range(0, partitions.size()).forEach(index -> addEntry(partitions.get(index), index));
        if(page >= this.pages.size()) page = Math.max(this.pages.size() - 1, 0);
        used();
    }

    private void addEntry(List<String> content, long index){
        pages.put(index, new Page(content));
    }

    public void remove(String... contents){
        Set<String> content = pages.values().stream()
                .map(Page::getContent)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        content.removeAll(Arrays.asList(contents));
        generate(new ArrayList<>(content));
        invalidateInventories();
        dispatchSyncBindedCommand();
    }

    public void add(String... contents){
        Set<String> content = pages.values().stream()
                .map(Page::getContent)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        content.addAll(Arrays.asList(contents));
        invalidateInventories();
        generate(new ArrayList<>(content));
    }

    public void invalidateInventories(){
        this.pages.values().forEach(page -> page.setInventory(null));
    }

    public Page current(){
        used();
        return pages.get(page);
    }

    public void next(){
        used();
        page++;
        dispatchSyncBindedCommand();
    }

    public void before(){
        used();
        page--;
        dispatchSyncBindedCommand();
    }

    private void dispatchSyncBindedCommand(){
        ArematicsExecutor.syncRun(() -> {
            if(sender instanceof Player && ((Player) sender).getOpenInventory() != null) {
                ((Player) sender).closeInventory();
                Bukkit.getServer().dispatchCommand(sender, bindedCommand);
            }
        });
    }

    public boolean hasNext(){
        return page < pages.size() - 1;
    }

    public boolean hasBefore(){
        return page > 0;
    }

    public void used(){
        this.lastUse = System.currentTimeMillis();
    }

    public int getMaxCacheSeconds() {
        return maxCacheSeconds;
    }

    long getLastUse(){
        return this.lastUse;
    }
}
