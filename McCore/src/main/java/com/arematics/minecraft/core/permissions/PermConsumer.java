package com.arematics.minecraft.core.permissions;

import org.bukkit.command.CommandSender;

import java.util.function.Consumer;

public interface PermConsumer {
    PermissionData ifPermitted(Consumer<CommandSender> permitted);
}
