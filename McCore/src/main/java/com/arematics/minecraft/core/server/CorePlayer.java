package com.arematics.minecraft.core.server;

import com.arematics.minecraft.core.currency.Currency;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CorePlayer {

    private Player player;

    CorePlayer(Player player){
        this.player = player;
    }

    private final Map<Currency, Double> currencies = new HashMap<>();

    public CorePlayer() {

    }

    public Map<Currency, Double> getCurrencies() {
        return currencies;
    }

    public Player getPlayer() {
        return player;
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
