package com.arematics.minecraft.core.messaging.injector;

public abstract class StringInjector extends Injector<String> {
    public abstract StringInjector replace(String pattern, String replace);
}
