package com.arematics.minecraft.core.server.entities;

public interface CurrencyEntity {

    /**
     * Get money from entity
     * @return Money Amount
     */
    long getMoney();

    /**
     * Set money for entity
     * @param money Amount to set
     */
    void setMoney(long money);

    /**
     * Add money to current money balance for entity
     * @param amount Amount to add
     */
    void addMoney(long amount);

    /**
     * Remove money from current money balance for entity
     * @param amount Amount to remove
     */
    void removeMoney(long amount) throws RuntimeException;
}
