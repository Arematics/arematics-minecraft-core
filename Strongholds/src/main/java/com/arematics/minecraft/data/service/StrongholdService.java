package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.Stronghold;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StrongholdService {

    @Autowired
    public StrongholdService(){}

    public Stronghold findById(String id){
        return new Stronghold(id);
    }
}
