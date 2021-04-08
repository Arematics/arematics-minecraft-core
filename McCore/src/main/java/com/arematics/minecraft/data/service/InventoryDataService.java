package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.InventoryData;
import com.arematics.minecraft.data.mode.repository.InventoryDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class InventoryDataService {

    private final InventoryDataRepository inventoryDataRepository;

    public List<String> findDataKeys(){
        return inventoryDataRepository.findDataKeys();
    }

    public InventoryData findDataByKey(String key){
        Optional<InventoryData> result = inventoryDataRepository.findByDataKey(key);
        if(!result.isPresent())
            throw new RuntimeException("Could not find data with key: " + key);
        return result.get();
    }

    public void delete(InventoryData data){
        inventoryDataRepository.delete(data);
    }
}
