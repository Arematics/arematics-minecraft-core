package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.CorePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "workbench", description = "open a portable workbench")
public class WorkbenchCommand extends CoreCommand {

    public WorkbenchCommand() {
        super("workbench", "wb");
    }

    @Override
    public void onDefaultExecute(CommandSender sender) {
        if(sender instanceof Player)
            ((Player) sender).openWorkbench(((Player) sender).getLocation(), true);
    }
}
