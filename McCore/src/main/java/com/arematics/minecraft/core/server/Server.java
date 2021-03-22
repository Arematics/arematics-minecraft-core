package com.arematics.minecraft.core.server;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.items.ItemUpdateClickListener;
import com.arematics.minecraft.core.server.currency.CurrencyController;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.items.CoreItemCreationModifier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class Server {

    private final CurrencyController currencyController;
    private final MoneyStatistics moneyStatistics;
    private final List<CoreItemCreationModifier> coreItemCreationModifiers;

    public CoreItem generate(Material material){
        return create(new ItemStack(material));
    }

    public CoreItem create(ItemStack item){
        if(item == null || item.getType() == Material.AIR) return null;
        CoreItem source = new CoreItem(item);
        for(CoreItemCreationModifier modifier : coreItemCreationModifiers) source = modifier.modify(source);
        return source;
    }

    public CoreItem generateNoModifier(Material material){
        return createNoModifier(new ItemStack(material));
    }

    public CoreItem createNoModifier(ItemStack item){
        if(item == null || item.getType() == Material.AIR) return null;
        return new CoreItem(item);
    }

    public List<CorePlayer> getOnline(){
        return Bukkit.getOnlinePlayers().stream().map(CorePlayer::get).collect(Collectors.toList());
    }

    public List<CorePlayer> getOnlineTeam(){
        return Bukkit.getOnlinePlayers().stream()
                .map(CorePlayer::get)
                .filter(player -> player.getUser().getRank().isInTeam())
                .collect(Collectors.toList());
    }

    public void registerItemListener(CorePlayer player, CoreItem item, Function<CoreItem, CoreItem> function){
        ItemUpdateClickListener listener = new ItemUpdateClickListener(item, function);
        player.inventories().addListener(listener);
        Bukkit.getPluginManager().registerEvents(listener, Boots.getBoot(CoreBoot.class));
    }

    public void tearDownItemListener(ItemUpdateClickListener listener){
        HandlerList.unregisterAll(listener);
    }

    public CorePlayer findOnline(UUID uuid){
        return CorePlayer.get(Bukkit.getPlayer(uuid));
    }
}
