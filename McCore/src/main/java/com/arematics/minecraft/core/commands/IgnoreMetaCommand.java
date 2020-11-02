package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.messaging.Messages;
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
