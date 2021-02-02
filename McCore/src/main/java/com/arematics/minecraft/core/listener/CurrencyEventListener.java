package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.events.CurrencyEvent;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.mode.model.CurrencyData;
import com.arematics.minecraft.data.service.CurrencyDataService;
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

    private final CurrencyDataService currencyDataService;

    @EventHandler(priority = EventPriority.MONITOR)
    public void safe(CurrencyEvent event){
        ArematicsExecutor.runAsync(() -> safeCurrencyEvent(event));
    }

    private void safeCurrencyEvent(CurrencyEvent event){
        CurrencyData data = new CurrencyData(event.getPlayer().getUUID(), Timestamp.valueOf(LocalDateTime.now()),
                event.getAmount(), event.getType(), event.getTarget(), !event.isCancelled());
        this.currencyDataService.save(data);
    }
}
