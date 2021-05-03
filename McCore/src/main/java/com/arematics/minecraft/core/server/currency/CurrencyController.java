package com.arematics.minecraft.core.server.currency;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.service.CurrencyDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class CurrencyController {

    private final CurrencyDataService currencyDataService;

    public ExecutionAmount createEvent(CorePlayer player){
        return CurrencyExecutionBuilder.create(player);
    }

    public CurrencyDataService service(){
        return currencyDataService;
    }
}
