package com.arematics.minecraft.core.messaging;

import org.bukkit.command.CommandSender;

import java.util.function.Supplier;

public interface MessageReplacement {
    MessageReplacement replace(String key, String value);
    void send(CommandSender... senders);
    void broadcast();
    String toString(CommandSender sender);
}
