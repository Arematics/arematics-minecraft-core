package com.arematics.minecraft.core.server.currency;

import com.arematics.minecraft.core.events.CurrencyEvent;
import com.arematics.minecraft.core.events.CurrencyEventType;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrencyExecutionBuilder implements ExecutionAmount, ExecutionType, ExecutionTarget, ExecutionSuccess {

    static ExecutionAmount create(CorePlayer executor){
        return new CurrencyExecutionBuilder(executor);
    }

    private final CorePlayer executor;
    private double amount;
    private CurrencyEventType type;
    private String target;

    @Override
    public ExecutionType setAmount(double amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public boolean onSuccess(Runnable runnable) {
        CurrencyEvent event = new CurrencyEvent(executor, amount, type, target);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled())
            try {
                runnable.run();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        return false;
    }

    @Override
    public boolean removeMoney() {
        return onSuccess(() -> executor.removeMoney(amount));
    }

    @Override
    public boolean addMoney() {
        return onSuccess(() -> executor.addMoney(amount));
    }

    @Override
    public ExecutionSuccess setTarget(String target) {
        this.target = target;
        return this;
    }

    @Override
    public ExecutionTarget setEventType(CurrencyEventType type) {
        this.type = type;
        return this;
    }
}
