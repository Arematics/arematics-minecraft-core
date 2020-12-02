package com.arematics.minecraft.crystals.logic;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.items.Items;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.data.mode.model.CrystalKey;
import org.springframework.stereotype.Component;

@Component
public class DefaultItemCrystalType extends CrystalType {

    @Override
    public String propertyValue() {
        return "Item";
    }

    @Override
    public void execute(CorePlayer player, CoreItem item, CrystalKey key) {
        try{
            CoreItem clone = CoreItem.create(item.clone());
            clone.getMeta().clearCustomNBT();
            clone.clearLore().clearName();
            Items.giveItem(player, clone);
            Part part = new Part(clone.getItemMeta().getDisplayName() != null ? clone.getItemMeta().getDisplayName() :
                    clone.getType().name())
                    .setHoverActionShowItem(clone);
            player.info("You received %items% from §7Magic Crystal §8(" + key.getTotalName() + "§8)")
                    .setInjector(AdvancedMessageInjector.class)
                    .replace("items", part)
                    .handle();
        }catch (RuntimeException re){
            player.failure("This key could not be found, please report to team").handle();
        }
    }
}
