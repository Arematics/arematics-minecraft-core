package com.arematics.minecraft.core.server.entities;

public interface CurrencyEntity {

    /**
     * Get money from entity
     * @return Money Amount
     */
    double getMoney();

    /**
     * Set money for entity
     * @param money Amount to set
     */
    void setMoney(double money);

    /**
     * Add money to current money balance for entity
     * @param amount Amount to add
     */
    void addMoney(double amount);

    /**
     * Remove money from current money balance for entity
     * @param amount Amount to remove
     */
    void removeMoney(double amount) throws RuntimeException;
}
