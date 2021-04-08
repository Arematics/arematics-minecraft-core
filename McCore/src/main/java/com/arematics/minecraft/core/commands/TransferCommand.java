package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.InventoryData;
import com.arematics.minecraft.data.service.GenericItemCollectionService;
import com.arematics.minecraft.data.service.InventoryDataService;
import com.arematics.minecraft.data.share.model.ItemCollection;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
        List<String> dataNames = inventoryDataService.findDataKeys();
        sender.info("listing")
                .DEFAULT()
                .replace("list_type", "Data Key")
                .replace("list_value", StringUtils.join(dataNames, ','));
    }

    public void transfer(CorePlayer sender, String key, boolean global){
        try{
            InventoryData data = inventoryDataService.findDataByKey(key);
            ItemCollection collection = genericItemCollectionService.findItemCollection(key);
            if(collection != null)
                sender.warn("Collection with same key already exists overwriting data").handle();
            collection = new ItemCollection(key, data.getItems());
            genericItemCollectionService.save(global, collection);
            inventoryDataService.delete(data);
            sender.info("Inventory Data transferred successful").handle();
        }catch (RuntimeException re){
            sender.warn(re.getMessage()).handle();
        }
    }
}
