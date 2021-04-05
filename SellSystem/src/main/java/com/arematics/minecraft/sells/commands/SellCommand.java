package com.arematics.minecraft.sells.commands;

import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.service.ItemPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SellCommand extends CoreCommand {

    private final ItemPriceService itemPriceService;

    @Autowired
    public SellCommand(ItemPriceService itemPriceService){
        super("sell");
        this.itemPriceService = itemPriceService;
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        new SellGUI(sender, server, itemPriceService);
    }
}
