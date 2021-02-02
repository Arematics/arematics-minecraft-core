package com.arematics.minecraft.crystals.logic;

import com.arematics.minecraft.core.events.CurrencyEvent;
import com.arematics.minecraft.core.events.CurrencyEventType;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.CrystalKey;
import org.bukkit.Bukkit;
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
            double value = Double.parseDouble(item.getMeta().getString(propertyValue()));
            CurrencyEvent event = new CurrencyEvent(player, value, CurrencyEventType.GENERATE, "crystal-key");
            Bukkit.getServer().getPluginManager().callEvent(event);
            if(!event.isCancelled()) {
                player.addMoney(value);
                player.info("You received " + value + " Coins from §7Magic Crystal §8(" + key.getTotalName() + "§8)").handle();
            }else
                player.warn("Your withdrawal could not be processed. Learn more in our security policy (/security)").handle();
        }catch (RuntimeException re){
            player.failure("Coins amount not valid, please report").handle();
        }
    }
}
