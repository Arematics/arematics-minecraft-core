package com.arematics.minecraft.core.server;

import com.arematics.minecraft.core.currency.Currency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CorePlayerTest extends Assertions {

    private Currency currency;
    private CorePlayer corePlayer;

    @BeforeEach
    void init(){
        currency = new Currency("COINS");
        corePlayer = new CorePlayer();
    }

    @Test
    void setCurrency() {
        assertEquals(0, corePlayer.getCurrency(currency), "Currency is not empty");
        corePlayer.setCurrency(currency, 500);
        assertEquals(500, corePlayer.getCurrency(currency), "Currency is not macthing 500");
    }

    @Test
    void getCurrency() {
        assertEquals(0, corePlayer.getCurrency(currency), "Currency is not empty");
    }

    @Test
    void addCurrency() {
        long start = System.currentTimeMillis();
        assertEquals(0, corePlayer.getCurrency(currency), "Currency is not empty");
        corePlayer.setCurrency(currency, 500);
        assertEquals(500, corePlayer.getCurrency(currency), "Currency is not matching 500");
        corePlayer.addCurrency(currency, 1500);
        assertEquals(2000, corePlayer.getCurrency(currency), "Currency is not matching 2000");
        assertTrue( (System.currentTimeMillis() - start) < 7,
                "Takes to long >= 7 Millisecond's - 2 Logging");
    }

    @Test
    void removeCurrency() {
        long start = System.currentTimeMillis();
        assertEquals(0, corePlayer.getCurrency(currency), "Currency is not empty");
        corePlayer.setCurrency(currency, 500);
        assertEquals(500, corePlayer.getCurrency(currency), "Currency is not matching 500");
        corePlayer.removeCurrency(currency, 250);
        assertEquals(250, corePlayer.getCurrency(currency), "Currency is not matching 250");
        assertTrue( (System.currentTimeMillis() - start) < 7,
                "Takes to long >= 7 Millisecond's - 2 Logging");
    }
}