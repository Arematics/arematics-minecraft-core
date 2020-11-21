package com.arematics.minecraft.core.bukkit.wrapper;

import com.arematics.minecraft.core.events.BaseEvent;
import com.arematics.minecraft.core.server.CorePlayer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AsyncPlayerDeathEvent extends BaseEvent {

    private final CorePlayer player;
    private final CorePlayer killer;
    private final List<ItemStack> drops;
    private final int droppedExp;
    private final int newExp;
    private final int newTotalExp;
    private final int newLevel;
    private final String deathMessage;

    public AsyncPlayerDeathEvent(CorePlayer player,
                                 CorePlayer killer,
                                 List<ItemStack> drops,
                                 int droppedExp,
                                 int newExp,
                                 int newTotalExp,
                                 int newLevel,
                                 String deathMessage){
        super(true);
        this.player = player;
        this.killer = killer;
        this.drops = drops;
        this.droppedExp = droppedExp;
        this.newExp = newExp;
        this.newTotalExp = newTotalExp;
        this.newLevel = newLevel;
        this.deathMessage = deathMessage;
    }
}
