package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.advanced.PartBuilder;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.CommandUtils;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.global.model.Warn;
import com.arematics.minecraft.data.service.WarnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "team.punishment.delwarn", description = "Del Warn for a player")
public class DelWarnCommand extends CoreCommand {
    
    private final WarnService warnService;
    
    @Autowired
    public DelWarnCommand(WarnService warnService){
        super("delwarn", true);
        this.warnService = warnService;
    }
    
    @SubCommand("{player}")
    public void subMethodNameReplace(CorePlayer sender, User target) {
        sender.info(CommandUtils.prettyHeader("Warns", target.getLastName())).DEFAULT().disableServerPrefix().handle();
        this.warnService.getWarns(target.getUuid()).forEach(warn -> sender.info("%replaced%")
                .setInjector(AdvancedMessageInjector.class)
                .replace("replaced", warnToMSG(warn))
                .disableServerPrefix()
                .handle());
    }

    @SubCommand("byId {id}")
    public void delWarnById(CorePlayer sender, Long id) {
        try{
            Warn warn = warnService.findById(id);
            warnService.remove(warn);
            sender.info("Warn has been deleted").handle();
        }catch (RuntimeException re){
            re.printStackTrace();
            throw new CommandProcessException("Warn with id: " + id + " not exists");
        }
    }

    private Part warnToMSG(Warn warn){
        return PartBuilder.createHoverAndSuggest("  §8Reason: §c" + warn.getReason() + " §8[" + warn.getAmount() + "]",
                "§cDelete warn",
                "/delwarn byId " + warn.getId());
    }
}
