package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Perm(permission = "ignore-meta", description = "Allowed to ignore item meta actions")
public class IgnoreMetaCommand extends CoreCommand {

    private static final List<Player> ignoreList = new ArrayList<>();

    public static boolean isIgnoreMeta(Player player){
        return ignoreList.contains(player);
    }

    public IgnoreMetaCommand(){
        super("ignore-meta");
    }

    @Override
    public boolean onDefaultExecute(CommandSender sender){
        List<String> subCommands = super.getSubCommands();
        Messages.create("cmd_not_valid")
                .to(sender)
                .setInjector(AdvancedMessageInjector.class)
                .eachReplace("cmd_usage", subCommands.toArray(new String[]{}))
                .setHover(HoverAction.SHOW_TEXT, "Open to chat")
                .setClick(ClickAction.SUGGEST_COMMAND, "/ignore-meta %value%")
                .END()
                .handle();
        return true;
    }

    @SubCommand("toggle")
    public boolean toggleIgnoreMeta(Player player) {
        if(ignoreList.contains(player)) disableIgnoreMeta(player);
        else enableIgnoreMeta(player);
        return true;
    }

    @SubCommand("enable")
    public boolean enableIgnoreMeta(Player player) {
        if(!ignoreList.contains(player)) ignoreList.add(player);
        Messages.create("Ignoring Item Meta has been enabled! Be careful.")
                .WARNING()
                .to(player)
                .handle();
        return true;
    }

    @SubCommand("disable")
    public boolean disableIgnoreMeta(Player player) {
        ignoreList.remove(player);
        Messages.create("Ignoring Item Meta has been disabled")
                .to(player)
                .handle();
        return true;
    }
}
