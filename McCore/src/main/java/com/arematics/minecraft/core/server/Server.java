package com.arematics.minecraft.core.server;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.items.ItemUpdateClickListener;
import com.arematics.minecraft.core.server.currency.CurrencyController;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.InventoryHandler;
import com.arematics.minecraft.core.server.entities.player.PlayerService;
import com.arematics.minecraft.core.server.items.Items;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Accessors(fluent = true)
@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class Server {

    private final ArematicsExecutor arematicsExecutor;
    private final PlayerService playerService;
    private final CurrencyController currencyController;
    private final MoneyStatistics moneyStatistics;
    private final Items items;

    public PlayerService players(){
        return playerService;
    }

    public ArematicsExecutor schedule(){
        return arematicsExecutor;
    }

    public List<CorePlayer> getOnline(){
        return Bukkit.getOnlinePlayers().stream().map(players()::fetchPlayer).collect(Collectors.toList());
    }

    public List<CorePlayer> onlineWithRank(Long id){
        return getOnline().stream()
                .filter(player -> player.getUser().getRank().getId().equals(id) || player.getUser().getDisplayRank().getId().equals(id))
                .collect(Collectors.toList());
    }

    public List<CorePlayer> getOnlineTeam(){
        return Bukkit.getOnlinePlayers().stream()
                .map(players()::fetchPlayer)
                .filter(player -> player.getUser().getRank().isInTeam())
                .collect(Collectors.toList());
    }

    public void registerItemListener(CorePlayer player, CoreItem item, Function<CoreItem, CoreItem> function){
        ItemUpdateClickListener listener = new ItemUpdateClickListener(this, item, null, function, player.handle(InventoryHandler.class).getView().getTopInventory());
        player.handle(InventoryHandler.class).addListener(listener);
        Bukkit.getPluginManager().registerEvents(listener, Boots.getBoot(CoreBoot.class));
    }

    public void registerItemListener(CorePlayer player, CoreItem item, ClickType type, Function<CoreItem, CoreItem> function){
        ItemUpdateClickListener listener = new ItemUpdateClickListener(this, item, type, function, player.handle(InventoryHandler.class).getView().getTopInventory());
        player.handle(InventoryHandler.class).addListener(listener);
        Bukkit.getPluginManager().registerEvents(listener, Boots.getBoot(CoreBoot.class));
    }

    public void tearDownItemListener(ItemUpdateClickListener listener){
        HandlerList.unregisterAll(listener);
    }

    public CorePlayer findOnline(UUID uuid){
        return players().fetchPlayer(Bukkit.getPlayer(uuid));
    }

    public void onStop(){
    }
}
