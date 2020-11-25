package com.arematics.minecraft.core.server;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.currency.Currency;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.messaging.MessageInjector;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.pages.Pager;
import com.arematics.minecraft.core.scoreboard.functions.BoardSet;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.core.utils.Inventories;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.mode.model.GameStats;
import com.arematics.minecraft.data.service.GameStatsService;
import com.arematics.minecraft.data.service.InventoryService;
import com.arematics.minecraft.data.service.UserService;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Data
public class CorePlayer{
    private static Map<UUID, CorePlayer> players = new HashMap<>();

    public static CorePlayer get(Player player){
        if(!players.containsKey(player.getUniqueId()))
            players.put(player.getUniqueId(), new CorePlayer(player));
        return players.get(player.getUniqueId());
    }

    public static void invalidate(Player player){
        if(players.containsKey(player.getUniqueId())) players.get(player.getUniqueId()).unload();
        players.remove(player.getUniqueId());
    }

    public static void unload(Player player){
        players.remove(player.getUniqueId()).unload();
    }

    private final Player player;
    private final Map<Currency, Double> currencies = new HashMap<>();
    private final Pager pager;
    private final BoardSet boardSet;
    private final PlayerRequestSettings requestSettings;
    private boolean ignoreMeta = false;
    private boolean disableLowerInventory = false;
    private boolean disableUpperInventory = false;

    private boolean inFight = false;

    private final GameStatsService service;
    private final UserService userService;

    public CorePlayer(Player player){
        this.player = player;
        this.pager = new Pager(this);
        this.boardSet = new BoardSet(player);
        this.userService = Boots.getBoot(CoreBoot.class).getContext().getBean(UserService.class);
        this.requestSettings = new PlayerRequestSettings(this);
        this.service = Boots.getBoot(CoreBoot.class).getContext().getBean(GameStatsService.class);
    }

    private void unload() {
        this.pager.unload();
        this.boardSet.remove();
    }

    public void setInFight(){
        this.inFight = true;
        ArematicsExecutor.asyncDelayed(this::fightEnd, 3, TimeUnit.SECONDS);
    }

    public void fightEnd(){
        this.inFight = false;
        this.info("Could log out now").handle();
    }

    public InventoryView getView(){
        return player.getOpenInventory();
    }

    public void openInventory(Inventory inventory){
        Inventories.openLowerDisabledInventory(inventory, this);
    }

    public void openTotalBlockedInventory(Inventory inventory){
        Inventories.openLowerDisabledInventory(inventory, this);
    }

    public void openLowerEnabledInventory(Inventory inventory){
        Inventories.openInventory(inventory, this);
    }

    public User getUser(){
        return this.userService.getOrCreateUser(this);
    }

    UserService getUserService(){
        return this.userService;
    }

    public void update(User user){
        this.userService.update(user);
    }

    public void addKarma(int amount){
        User user = getUser();
        user.setKarma(user.getKarma() + amount);
        update(user);
    }

    public void removeKarma(int amount){
        User user = getUser();
        user.setKarma(user.getKarma() - amount);
        update(user);
    }

    public PlayerRequestSettings getRequestSettings(){
        return this.requestSettings;
    }

    public MessageInjector info(String msg){
        return Messages.create(msg)
                .to(this.getPlayer());
    }

    public MessageInjector warn(String msg){
        return Messages.create(msg)
                .WARNING()
                .to(this.getPlayer());
    }

    public MessageInjector failure(String msg){
        return Messages.create(msg)
                .FAILURE()
                .to(this.getPlayer());
    }

    public BoardSet getBoard(){
        return this.boardSet;
    }

    public GameStats getStats(){
        return this.service.getOrCreate(getUUID());
    }

    private void saveStats(GameStats stats){
        this.service.save(stats);
    }

    public void cleanStats(){
        this.service.delete(getStats());
    }

    public void onStats(Consumer<GameStats> execute){
        GameStats stats = getStats();
        execute.accept(stats);
        saveStats(stats);
    }

    public void setKills(int kills){
        onStats(stats -> stats.setKills(kills));
    }

    public void addKill(){
        onStats(stats -> stats.setKills(stats.getKills() + 1));
    }

    public void setDeaths(int deaths){
        onStats(stats -> stats.setDeaths(deaths));
    }

    public void addDeath(){
        onStats(stats -> stats.setDeaths(stats.getDeaths() + 1));
    }

    public void setBounty(int bounty){
        onStats(stats -> stats.setBounty(bounty));
    }

    public UUID getUUID(){
        return this.player.getUniqueId();
    }

    public CoreItem getItemInHand(){
        return CoreItem.create(player.getItemInHand());
    }

    public Inventory getInventory(InventoryService service, String key) throws RuntimeException{
        return service.getInventory(player.getUniqueId() + "." + key);
    }

    public Inventory getOrCreateInventory(InventoryService service, String key, String title, byte slots){
        return service.getOrCreate(player.getUniqueId() + "." + key, title, slots);
    }

    public Location getLocation() {
        return this.player.getLocation();
    }

    /**
     * Call setCurrency Async with CompletableFuture
     * @param currency Currency Type
     * @param amount New Currency Value
     */
    public void setCurrency(Currency currency, double amount){
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> setSyncCurrency(currency, amount));
        try{
            future.get();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Call getCurrency Async with CompletableFuture
     * @param currency Currency Type
     */
    public double getCurrency(Currency currency){
        CompletableFuture<Double> completableFuture = CompletableFuture.supplyAsync(() -> getSyncCurrency(currency));
        try{
            return completableFuture.get();
        }catch (Exception e){
            return 0.0;
        }
    }

    /**
     * Call addCurrency Async with CompletableFuture
     * @param currency Currency Type
     * @param amount Added Currency Value
     */
    public void addCurrency(Currency currency, double amount){
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> addSyncCurrency(currency, amount));
        try{
            future.get();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Call removeCurrency Async with CompletableFuture
     * @param currency Currency Type
     * @param amount Remove Currency Value
     */
    public void removeCurrency(Currency currency, double amount){
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> removeSyncCurrency(currency, amount));
        try{
            future.get();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * This method sets the value for a specific currency and returns the old value
     * @param currency Currency Type
     * @param amount New Value of Currency
     */
    private void setSyncCurrency(Currency currency, double amount){
        this.getCurrencies().put(currency, amount);
    }

    /**
     * Returns the Value of an specific Currency
     * @param currency Currency Type
     * @return Currency Value
     */
    private double getSyncCurrency(Currency currency){
        return this.getCurrencies().getOrDefault(currency, 0.0);
    }

    /**
     * Adds currency amount to current currency value
     * @param currency Currency Type
     * @param amount Added amount of Currency
     */
    private void addSyncCurrency(Currency currency, double amount){
        this.setCurrency(currency, getCurrency(currency) + amount);
    }

    /**
     * Removes currency amount from current currency value
     * @param currency Currency Type
     * @param amount Removed amount of Currency, sets currency to 0 if it would get negative.
     */
    private void removeSyncCurrency(Currency currency, double amount){
        double now = getCurrency(currency);
        if((now - amount) < 0) this.setCurrency(currency, 0);
        else this.setCurrency(currency, now - amount);
    }
}
