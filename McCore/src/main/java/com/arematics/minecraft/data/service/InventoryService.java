package com.arematics.minecraft.data.service;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.data.mode.model.InventoryData;
import com.arematics.minecraft.data.mode.repository.InventoryDataRepository;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class InventoryService {

    private final InventoryDataRepository repository;
    private final Map<String, Long> idMap = new HashMap<>();
    private final Map<String, Inventory> inventories = new HashMap<>();

    @Autowired
    public InventoryService(InventoryDataRepository inventoryDataRepository){
        this.repository = inventoryDataRepository;
    }

    public Inventory getInventory(String key){
        if(inventories.containsKey(key))
            return inventories.get(key);
        Optional<InventoryData> data = repository.findByDataKey(key);
        if(!data.isPresent()) throw new RuntimeException("Inventory with key: " + key + " could not be found");
        return fromData(data.get());
    }

    public Inventory getOrCreate(String key, String title, byte slots){
        if(inventories.containsKey(key))
            return patchSlotsOrTitle(key, title, slots);
        Optional<InventoryData> data = repository.findByDataKey(key);
        if(!data.isPresent())
            data = Optional.of(saveNew(key, title, slots));
        InventoryData d = data.get();
        d.setTitle(title);
        d.setSlots(slots);
        saveRaw(d);
        return fromData(d);
    }

    private Inventory patchSlotsOrTitle(String key, String title, byte slots){
        Inventory inv = inventories.get(key);
        if(!inv.getTitle().equals(title) || inv.getSize() != slots) {
            Inventory newInv = Bukkit.createInventory(null, slots, title);
            newInv.setContents(inv.getContents());
            inventories.replace(key, newInv);
        }

        return inventories.get(key);
    }

    public InventoryData saveNew(String key, String title, int slots){
        return repository.save(new InventoryData(null, key, title, slots, new CoreItem[]{}));
    }

    private void saveRaw(InventoryData data){
        repository.save(data);
    }

    public void save(String key){
        repository.save(mapToData(key));
    }

    public void delete(String key){
        Optional<InventoryData> data = repository.findByDataKey(key);
        if(data.isPresent()) {
            repository.delete(data.get());
            idMap.remove(key);
            inventories.remove(key);
        }
    }

    private Inventory fromData(InventoryData data){
        idMap.put(data.getDataKey(), data.getId());
        Inventory inv = Bukkit.createInventory(null, data.getSlots(), data.getTitle());
        inv.setContents(data.getItems());
        inventories.put(data.getDataKey(), inv);
        return inv;
    }

    private InventoryData mapToData(String key){
        InventoryData data = new InventoryData();
        data.setDataKey(key);
        data.setId(idMap.getOrDefault(key, null));
        Inventory inv = inventories.get(key);
        data.setSlots((byte) inv.getSize());
        data.setTitle(inv.getTitle());
        data.setItems(CoreItem.create(inv.getContents()));
        return data;
    }

    public Map<String, Inventory> getInventories() {
        return inventories;
    }
}
