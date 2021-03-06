package com.arematics.minecraft.crystals.logic;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.items.parser.ItemType;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.CrystalKey;
import com.arematics.minecraft.data.service.CrystalKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class CrystalItemType extends ItemType {

    private final Server server;
    private final CrystalKeyService service;

    @Override
    public String propertyValue() {
        return "CrystalItem";
    }

    @Override
    public Part execute(CorePlayer player, CoreItem item) {
        try{
            CrystalKey key = service.findById(item.getMeta().getString(propertyValue()));
            server.items().giveItemTo(player, CrystalKeyItem.fromKey(key));
            return new Part("§7Magic Crystal§8(" + key.getTotalName() + "§8)");
        }catch (RuntimeException re){
            player.failure("This key could not be found, please report to team").handle();
        }
        throw new RuntimeException("Something went wrong");
    }
}
