package com.arematics.minecraft.core.server.currency;

import com.arematics.minecraft.core.events.CurrencyEventType;

public interface ExecutionType {
    ExecutionTarget setEventType(CurrencyEventType type);
}
