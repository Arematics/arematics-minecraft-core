package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.messaging.advanced.MSGBuilder;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.advanced.PartBuilder;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.InventoryData;
import com.arematics.minecraft.data.service.GenericItemCollectionService;
import com.arematics.minecraft.data.service.InventoryDataService;
import com.arematics.minecraft.data.share.model.ItemCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Perm(permission = "inventories.transferold", description = "Permissions to transfer old inventories into new system")
public class TransferCommand extends CoreCommand {

    private final GenericItemCollectionService genericItemCollectionService;
    private final InventoryDataService inventoryDataService;

    @Autowired
    public TransferCommand(GenericItemCollectionService genericItemCollectionService,
                           InventoryDataService inventoryDataService){
        super("transfer");
        this.genericItemCollectionService = genericItemCollectionService;
        this.inventoryDataService = inventoryDataService;
    }

    @SubCommand("{key}")
    public void transferInventory(CorePlayer sender, String key) {
        transfer(sender, key, false);
    }

    @SubCommand("global {key}")
    public void transferToGlobalData(CorePlayer sender, String key) {
        transfer(sender, key, true);
    }

    @SubCommand("list data names")
    public void listExistingOldData(CorePlayer sender) {
        List<Part> parts = inventoryDataService.findDataKeys().stream().map(this::fetchPart).collect(Collectors.toList());
        sender.info("listing")
                .setInjector(AdvancedMessageInjector.class)
                .replace("list_type", new Part("Data Key"))
                .replace("list_value", MSGBuilder.join(parts, ','))
        .handle();
    }

    @SubCommand("list data names {startsWith}")
    public void listExistingOldData(CorePlayer sender, String startsWith) {
        List<Part> parts = inventoryDataService.findDataKeys(startsWith).stream().map(this::fetchPart).collect(Collectors.toList());
        sender.info("listing")
                .setInjector(AdvancedMessageInjector.class)
                .replace("list_type", new Part("Data Key"))
                .replace("list_value", MSGBuilder.join(parts, ','))
                .handle();
    }

    private Part fetchPart(String name){
        return PartBuilder.createHoverAndSuggest("§c" + name,
                "Generate transfer command for inventory " + name, "/transfer " + name);
    }

    public void transfer(CorePlayer sender, String key, boolean global){
        InventoryData data;
        try{
            data = inventoryDataService.findDataByKey(key);
        }catch (RuntimeException re){
            re.printStackTrace();
            throw new CommandProcessException("No inventory data found for given key");
        }

        ItemCollection collection = null;
        try{
            collection = genericItemCollectionService.findItemCollection(key);
        }catch (RuntimeException ignore){}
        if(collection != null)
            sender.warn("Collection with same key already exists overwriting data").handle();
        collection = new ItemCollection(key, data.getItems());
        genericItemCollectionService.save(global, collection);
        inventoryDataService.delete(data);
        sender.info("Inventory Data transferred successful").handle();
    }
}
