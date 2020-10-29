package com.arematics.minecraft.data.service;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.data.mode.model.InventoryData;
import com.arematics.minecraft.data.mode.repository.InventoryDataRepository;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class InventoryService {

    private final InventoryDataRepository repository;
    private Map<String, Long> idMap = new HashMap<>();
    private Map<String, Inventory> inventories = new HashMap<>();

    @Autowired
    public InventoryService(InventoryDataRepository inventoryDataRepository){
        this.repository = inventoryDataRepository;
    }

    public List<String> findKeys(String input){
        return repository.findAllKeysBy(input);
    }

    public Inventory getInventory(String key){
        if(inventories.containsKey(key)) return inventories.get(key);
        Optional<InventoryData> data = repository.findByKey(key);
        if(!data.isPresent()) throw new RuntimeException("Inventory with key: " + key + " could not be found");
        return fromData(data.get());
    }

    public void save(String key){
        repository.save(mapToData(key));
    }

    private Inventory fromData(InventoryData data){
        idMap.put(data.getKey(), data.getId());
        Inventory inv = Bukkit.createInventory(null, data.getSlots(), data.getTitle());
        inv.setContents(data.getItems());
        inventories.put(data.getKey(), inv);
        return inv;
    }

    private InventoryData mapToData(String key){
        InventoryData data = new InventoryData();
        data.setKey(key);
        data.setId(idMap.getOrDefault(key, null));
        Inventory inv = inventories.get(key);
        data.setSlots((byte) inv.getSize());
        data.setTitle(inv.getTitle());
        data.setItems(CoreItem.create(inv.getContents()));
        return data;
    }
}
