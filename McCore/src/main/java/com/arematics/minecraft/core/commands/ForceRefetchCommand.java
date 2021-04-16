package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.service.CommandRedirectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "team.chat.refetch")
public class ForceRefetchCommand extends CoreCommand {

    private final CommandRedirectService commandRedirectService;

    @Autowired
    public ForceRefetchCommand(CommandRedirectService commandRedirectService){
        super("refetch-cmd");
        this.commandRedirectService = commandRedirectService;
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        commandRedirectService.forceRefetch();
        sender.info("Commands have been refetched").handle();
    }
}
