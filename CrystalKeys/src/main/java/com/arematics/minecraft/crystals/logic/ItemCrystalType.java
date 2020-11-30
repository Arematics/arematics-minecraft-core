package com.arematics.minecraft.crystals.logic;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.items.Items;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.data.mode.model.CrystalKey;
import com.arematics.minecraft.data.service.CrystalKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ItemCrystalType extends CrystalType {

    private final CrystalKeyService service;

    @Autowired
    public ItemCrystalType(CrystalKeyService crystalKeyService){
        this.service = crystalKeyService;
    }

    @Override
    public String propertyValue() {
        return "CrystalItem";
    }

    @Override
    public void execute(CorePlayer player, CoreItem item) {
        try{
            CrystalKey key = service.findById(item.getMeta().getString(propertyValue()));
            Items.giveItem(player, CrystalKeyItem.fromKey(key));
            player.info("You received a §7Magic Crystal§8(" + key.getTotalName() + "§8)").handle();
        }catch (RuntimeException re){
            player.failure("This key could not be found, please report to team").handle();
        }
    }
}
