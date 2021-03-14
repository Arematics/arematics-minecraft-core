package com.arematics.minecraft.core.items.parser;

import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.events.CurrencyEvent;
import com.arematics.minecraft.core.events.CurrencyEventType;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.Bukkit;
import org.springframework.stereotype.Component;

@Component
public class CoinsItemType extends ItemType {

    @Override
    public String propertyValue() {
        return "Money";
    }

    @Override
    public Part execute(CorePlayer player, CoreItem item) {
        try{
            double value = Double.parseDouble(item.getMeta().getString(propertyValue()));
            CurrencyEvent event = new CurrencyEvent(player, value, CurrencyEventType.GENERATE, "from-item");
            Bukkit.getServer().getPluginManager().callEvent(event);
            if(!event.isCancelled()) {
                player.addMoney(value);
                return new Part(value + " Coins");
            }else
                throw new CommandProcessException("Your withdrawal could not be processed. " +
                        "Learn more in our security policy (/security)");
        }catch (RuntimeException re){
            player.failure("Coins amount not valid, please report").handle();
        }
        throw new RuntimeException("Something went wrong");
    }
}
