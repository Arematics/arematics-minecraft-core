package com.arematics.minecraft.core.items.parser;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.items.Items;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.stereotype.Component;

@Component
public class DefaultItemType extends ItemType {

    @Override
    public String propertyValue() {
        return "Item";
    }

    @Override
    public Part execute(CorePlayer player, CoreItem item) {
        try{
            CoreItem clone = CoreItem.create(item.clone());
            clone.getMeta().clearCustomNBT();
            clone.clearLore().clearName();
            Items.giveItem(player, clone);
            return new Part(clone.getItemMeta().getDisplayName() != null ? clone.getItemMeta().getDisplayName() :
                    clone.getType().name())
                    .setHoverActionShowItem(clone);
        }catch (RuntimeException re){
            player.failure("This key could not be found, please report to team").handle();
        }
        throw new RuntimeException("Something went wrong");
    }
}
