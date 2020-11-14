package com.arematics.minecraft.core.command.supplier.page;

import com.arematics.minecraft.core.pages.Page;
import com.arematics.minecraft.core.server.CorePlayer;

import java.util.function.BiFunction;

public interface PageCommandUI {
    PageCommandAccept setUI(BiFunction<CorePlayer, Page, Boolean> onUI);
}
