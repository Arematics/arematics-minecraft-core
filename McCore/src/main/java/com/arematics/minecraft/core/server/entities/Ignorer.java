package com.arematics.minecraft.core.server.entities;

import java.util.UUID;

public interface Ignorer {

    UUID getUUID();

    boolean hasIgnored(UUID uuid);

    boolean ignore(UUID uuid);

    boolean unignore(UUID uuid);
}
