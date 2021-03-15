package com.arematics.minecraft.crystals.logic;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.items.parser.ItemMetaParser;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.CrystalKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CrystalMetaParser {

    private final ItemMetaParser itemMetaParser;

    @Autowired
    public CrystalMetaParser(ItemMetaParser itemMetaParser){
        this.itemMetaParser = itemMetaParser;
    }

    public void parse(CorePlayer player, CoreItem coreItem, CrystalKey crystalKey) throws RuntimeException{
        Part part = this.itemMetaParser.parse(player, coreItem);
        player.info("You received %value% from §7Magic Crystal §8(" + crystalKey.getTotalName() + "§8)")
                .setInjector(AdvancedMessageInjector.class)
                .replace("value", part)
                .handle();
    }
}
