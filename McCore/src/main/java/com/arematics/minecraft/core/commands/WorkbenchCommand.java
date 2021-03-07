package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "workbench", description = "open a portable workbench")
public class WorkbenchCommand extends CoreCommand {

    public WorkbenchCommand() {
        super("workbench", "wb");
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        sender.getPlayer().openWorkbench(sender.getLocation(), true);
    }
}
