package com.arematics.minecraft.core.permissions;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;

import java.util.function.Consumer;

public interface PermConsumer {
    PermissionData ifPermitted(Consumer<CorePlayer> permitted);
}
