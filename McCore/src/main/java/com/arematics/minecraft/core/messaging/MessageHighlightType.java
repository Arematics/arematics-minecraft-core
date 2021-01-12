package com.arematics.minecraft.core.messaging;

import org.bukkit.command.CommandSender;

public interface MessageHighlightType {
    MessageReciever WARNING();
    MessageReciever FAILURE();
    MessageInjector to(CommandSender... senders);
    MessageInjector broadcast();
}
