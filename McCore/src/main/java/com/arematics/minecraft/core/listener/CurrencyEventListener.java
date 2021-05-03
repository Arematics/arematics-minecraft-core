package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.events.CurrencyEvent;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.data.mode.model.CurrencyData;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor(onConstructor_= @Autowired)
public class CurrencyEventListener implements Listener {

    private final Server server;

    @EventHandler(priority = EventPriority.MONITOR)
    public void safe(CurrencyEvent event){
        server.schedule().runAsync(() -> safeCurrencyEvent(event));
    }

    private void safeCurrencyEvent(CurrencyEvent event){
        CurrencyData data = new CurrencyData(null, event.getPlayer().getUUID(), Timestamp.valueOf(LocalDateTime.now()),
                event.getAmount(), event.getType(), event.getTarget(), !event.isCancelled());
        switch (event.getType()){
            case GENERATE:
                server.moneyStatistics().addMoneyGenerate(event.getAmount());
                break;
            case WASTE:
                server.moneyStatistics().addMoneyRemoved(event.getAmount());
        }
        server.currencyController().service().save(data);
    }
}
