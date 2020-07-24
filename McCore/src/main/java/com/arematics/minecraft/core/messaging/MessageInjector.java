package com.arematics.minecraft.core.messaging;

import com.arematics.minecraft.core.messaging.injector.Injector;
import com.arematics.minecraft.core.messaging.injector.StringInjector;

public interface MessageInjector {
    void handle();
    StringInjector DEFAULT();
    <T extends Injector<?>> T setInjector(Class<T> injector);
}
