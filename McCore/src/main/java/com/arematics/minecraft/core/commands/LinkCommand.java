package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.global.model.AccountLink;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.AccountLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "player.management.linking", description = "Permission to manage links")
public class LinkCommand extends CoreCommand {

    private final AccountLinkService service;

    @Autowired
    public LinkCommand(AccountLinkService accountLinkService){
        super("link");
        this.service = accountLinkService;
    }

    @SubCommand("{first} {second}")
    public void linkAccounts(CorePlayer sender, User one, User two) {
        if(service.isLinkExistsBoth(one.getUuid(), two.getUuid()))
            throw new CommandProcessException("Accounts already linked");
        AccountLink link = new AccountLink(one.getUuid(), two.getUuid(), "Command", null);
        service.save(link);
        sender.info("Accounts have been linked").handle();
    }

    @SubCommand("info {user}")
    public void userLinkInfos(CorePlayer sender, User user){

    }

    @SubCommand("unlink {first} {second}")
    public void unlinkAccounts(CorePlayer sender, User one, User two) {
        if(!service.isLinkExistsBoth(one.getUuid(), two.getUuid()))
            throw new CommandProcessException("Accounts not linked");
        try{
            AccountLink link = service.findOneOfBoth(one.getUuid(), two.getUuid());
            service.remove(link);
            sender.info("Removed link for accounts");
        }catch (RuntimeException re){
            sender.warn("Account link could not be found");
        }
    }
}
