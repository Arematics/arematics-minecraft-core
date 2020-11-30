package com.arematics.minecraft.crystals.logic;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.CorePlayer;
import org.springframework.stereotype.Component;

@Component
public class CoinsCrystalType extends CrystalType {

    @Override
    public String propertyValue() {
        return "Money";
    }

    @Override
    public void execute(CorePlayer player, CoreItem item) {
        try{
            long value = Long.parseLong(item.getMeta().getString(propertyValue()));
            player.addMoney(value);
            player.info("You received items from your crystal key").handle();
        }catch (RuntimeException re){
            player.failure("Coins amount not valid, please report").handle();
        }
    }
}
