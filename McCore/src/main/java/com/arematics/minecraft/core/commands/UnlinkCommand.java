package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.global.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "player.management.linking.unlink", description = "Permission to unlink user accounts")
public class UnlinkCommand extends CoreCommand {

    private final LinkCommand linkCommand;

    @Autowired
    public UnlinkCommand(LinkCommand linkCommand){
        super("unlink");
        this.linkCommand = linkCommand;
    }

    @SubCommand("{first} {second}")
    public void unlinkAccounts(CorePlayer sender, User one, User two) throws InterruptedException {
        this.linkCommand.unlinkAccounts(sender, one, two);
    }
}
