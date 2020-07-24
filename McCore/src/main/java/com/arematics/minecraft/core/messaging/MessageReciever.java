package com.arematics.minecraft.core.messaging;

import org.bukkit.command.CommandSender;

public interface MessageReciever {
    MessageInjector to(CommandSender... senders);
}
