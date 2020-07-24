package com.arematics.minecraft.core.messaging.injector;

import org.bukkit.command.CommandSender;

public abstract class Injector<T> {
    public abstract void handle();
    protected abstract String prepareMessage(CommandSender sender);
    protected abstract T injectValues(String income);
}
