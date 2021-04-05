package com.arematics.minecraft.sells.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.ItemPrice;
import com.arematics.minecraft.data.service.ItemPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "sells.manage")
public class ManageSellPriceCommand extends CoreCommand {

    private final ItemPriceService service;

    @Autowired
    public ManageSellPriceCommand(ItemPriceService itemPriceService){
        super("setprice");
        this.service = itemPriceService;
    }

    @SubCommand("{price}")
    public void setSellPrice(CorePlayer sender, Double amount) {
        CoreItem hand = sender.getItemInHand();
        if(hand == null)
            throw new CommandProcessException("no_item_in_hand");
        int id = hand.getData().getItemTypeId();
        byte data = hand.getData().getData();
        String key = id + ":" + data;
        ItemPrice itemPrice;
        try{
            itemPrice = service.findItemPrice(key);
        }catch (RuntimeException re){
            itemPrice = new ItemPrice(key, amount);
        }
        service.save(itemPrice);
        sender.info("Item Price for item in hand set to §e" + amount + " §acoins").handle();
    }
}
