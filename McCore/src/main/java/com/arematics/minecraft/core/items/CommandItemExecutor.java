package com.arematics.minecraft.core.items;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.Bukkit;
import org.springframework.stereotype.Component;

@Component
public class CommandItemExecutor extends ItemClickExecutor {

    public CommandItemExecutor() {
        super("binded_command");
    }

    @Override
    public boolean execute(CorePlayer clicked, CoreItem coreItem) {
        Bukkit.getServer().dispatchCommand(clicked.getPlayer(), getMetaValue(coreItem));
        return true;
    }
}
