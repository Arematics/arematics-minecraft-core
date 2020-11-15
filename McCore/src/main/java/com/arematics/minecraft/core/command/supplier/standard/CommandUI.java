package com.arematics.minecraft.core.command.supplier.standard;

import com.arematics.minecraft.core.server.CorePlayer;

import java.util.function.Function;

public interface CommandUI {
    CommandAccept setUI(Function<CorePlayer, Boolean> onUI);
}
