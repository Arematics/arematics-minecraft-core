package com.arematics.minecraft.core.server.entities.player;

import java.util.function.Consumer;

public interface PermConsumer {
    PermissionData ifPermitted(Consumer<CorePlayer> permitted);
}
