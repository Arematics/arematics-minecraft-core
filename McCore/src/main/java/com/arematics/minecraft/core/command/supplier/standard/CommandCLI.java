package com.arematics.minecraft.core.command.supplier.standard;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;

import java.util.function.Consumer;

public interface CommandCLI {
    CommandUI setCLI(Consumer<CorePlayer> onCli);
}
