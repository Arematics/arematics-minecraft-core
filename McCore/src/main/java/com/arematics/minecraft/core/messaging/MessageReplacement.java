package com.arematics.minecraft.core.messaging;

import org.bukkit.command.CommandSender;

import java.util.function.Supplier;

public interface MessageReplacement {
    MessageReplacement replace(String key, Supplier<String> supplier);
    MessageReplacement replaceNext(Supplier<String> supplier);
    void send(CommandSender... senders);
    void broadcast();
    String toString(CommandSender sender);
}
