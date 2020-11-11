package com.arematics.minecraft.core.command;

import org.bukkit.entity.Player;

import java.util.function.Function;

public interface CommandUI {
    CommandAccept setUI(Function<Player, Boolean> onUI);
}
