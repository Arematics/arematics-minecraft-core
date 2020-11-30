package com.arematics.minecraft.crystals.logic;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.CorePlayer;
import org.springframework.stereotype.Component;

@Component
public class DefaultItemCrystalType extends CrystalType {

    @Override
    public String propertyValue() {
        return "Item";
    }

    @Override
    public void execute(CorePlayer player, CoreItem item) {
        try{
            CoreItem clone = CoreItem.create(item.clone());
            clone.getMeta().clearCustomNBT();
            clone.clearLore().clearName();
            player.getPlayer().getInventory().addItem(clone);
            player.info("You received items from your crystal key").handle();
        }catch (RuntimeException re){
            player.failure("This key could not be found, please report to team").handle();
        }
    }
}
