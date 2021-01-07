package com.arematics.minecraft.core.command.supplier.page;

import com.arematics.minecraft.core.pages.Page;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;

import java.util.function.BiFunction;

public interface PageCommandCLI {
    PageCommandUI setCLI(BiFunction<CorePlayer, Page, Boolean> onCli);
}
