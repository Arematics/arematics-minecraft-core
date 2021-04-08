package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.share.model.ItemCollection;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class GenericItemCollectionService {

    private final GlobalItemCollectionService globalItemCollectionService;
    private final ItemCollectionService itemCollectionService;

    public ItemCollection findItemCollection(String key){
        try{
            return itemCollectionService.findItemCollection(key);
        }catch (RuntimeException re){
            return globalItemCollectionService.findItemCollection(key);
        }
    }

    public ItemCollection save(boolean global, ItemCollection collection){
        return global ? globalItemCollectionService.save(collection) : itemCollectionService.save(collection);
    }
}
