package com.arematics.minecraft.core.server.items.parser;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class DefaultItemType extends ItemType {

    private final Server server;

    @Override
    public String propertyValue() {
        return "Item";
    }

    @Override
    public Part execute(CorePlayer player, CoreItem item) {
        try{
            CoreItem clone = server.items().create(item.clone());
            clone.getMeta().clearCustomNBT();
            clone.clearLore().clearName();
            server.items().giveItemTo(player, clone);
            return new Part(clone.getItemMeta().getDisplayName() != null ? clone.getItemMeta().getDisplayName() :
                    clone.getType().name())
                    .setHoverActionShowItem(clone);
        }catch (RuntimeException re){
            player.failure("This key could not be found, please report to team").handle();
        }
        throw new RuntimeException("Something went wrong");
    }
}
