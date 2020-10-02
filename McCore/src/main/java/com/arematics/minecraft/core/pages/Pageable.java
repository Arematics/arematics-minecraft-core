package com.arematics.minecraft.core.pages;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Pageable {

    private final static int maxItems = 36;

    private final Map<Long, Page> pages = new HashMap<>();
    private final CommandSender sender;
    private final String bindedCommand;
    private long page = 0;
    private long lastUse;

    public Pageable(List<String> content, CommandSender sender, String bindedCommand){
        this.sender = sender;
        this.bindedCommand = bindedCommand;
        List<List<String>> partitions = Lists.partition(content, 36);
        IntStream.range(0, partitions.size()).forEach(index -> addEntry(partitions.get(index), index));
        used();
    }

    private boolean addEntry(List<String> content, long index){
        pages.put(index, new Page(content));
        System.out.println("added entry");
        return true;
    }

    public Page current(){
        used();
        return pages.get(page);
    }

    public void next(){
        used();
        page++;
        Bukkit.getServer().dispatchCommand(sender, bindedCommand);
    }

    public void before(){
        used();
        page--;
        Bukkit.getServer().dispatchCommand(sender, bindedCommand);
    }

    public boolean hasNext(){
        return page != pages.size() - 1;
    }

    public boolean hasBefore(){
        return page > 0;
    }

    public void used(){
        this.lastUse = System.currentTimeMillis();
    }

    long getLastUse(){
        return this.lastUse;
    }
}
