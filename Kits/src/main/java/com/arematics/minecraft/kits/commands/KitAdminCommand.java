package com.arematics.minecraft.kits.commands;

import com.arematics.minecraft.core.annotations.Default;
import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.PluginCommand;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.data.share.model.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

import java.time.Period;

@Component
@PluginCommand(aliases = "kitmgr")
@Perm(permission = "kitadm", description = "Permission to full Kit Administration Command")
public class KitAdminCommand extends CoreCommand {

    public KitAdminCommand(){
        super("kitadm");
    }

    @Default
    public boolean sendInfo(CommandSender sender){
        Messages.create("cmd_not_valid")
                .to(sender)
                .DEFAULT()
                .replace("cmd_usage", "/kitadm create {name} {permission} {cooldown} {minPlayTime}")
                .handle();
        return true;
    }

    @SubCommand("create {name} {permission} {cooldown} {minPlayTime}")
    public boolean subMethodNameReplace(Player player, String name, Permission permission,
                                        Period cooldown, long minPlayTime) {
        return true;
    }
}
