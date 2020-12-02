package com.arematics.minecraft.crystals.logic;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.data.mode.model.CrystalKey;
import org.springframework.stereotype.Component;

@Component
public class CoinsCrystalType extends CrystalType {

    @Override
    public String propertyValue() {
        return "Money";
    }

    @Override
    public void execute(CorePlayer player, CoreItem item, CrystalKey key) {
        try{
            long value = Long.parseLong(item.getMeta().getString(propertyValue()));
            player.addMoney(value);
            player.info("You received " + value + " Coins from §7Magic Crystal §8(" + key.getTotalName() + "§8)").handle();
        }catch (RuntimeException re){
            player.failure("Coins amount not valid, please report").handle();
        }
    }
}
