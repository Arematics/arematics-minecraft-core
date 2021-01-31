package com.arematics.minecraft.core.events;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;

@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class CurrencyEvent extends PlayerEvent implements Cancellable {
    private final double amount;
    private final CurrencyEventType type;
    private final String target;

    private boolean cancelled;

    public CurrencyEvent(CorePlayer player, double amount, CurrencyEventType type, String target){
        super(player);
        this.amount = amount;
        this.type = type;
        this.target = target;
    }
}
