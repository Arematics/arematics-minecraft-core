package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.CurrencyData;
import com.arematics.minecraft.data.mode.repository.CurrencyDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_= {@Autowired})
public class CurrencyDataService {

    private final CurrencyDataRepository currencyDataRepository;

    public void save(CurrencyData data){
        currencyDataRepository.save(data);
    }
}
