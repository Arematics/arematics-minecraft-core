package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.language.LanguageAPI;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.utils.TitleAPI;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Perm(permission = "ignore-meta", description = "Allowed to ignore item meta actions")
public class IgnoreMetaCommand extends CoreCommand {

    private static final Set<Player> ignoreList = new HashSet<>();

    public static boolean isIgnoreMeta(Player player){
        return ignoreList.contains(player);
    }

    public static void setIgnoreMeta(Player player){
        ignoreList.add(player);
        TitleAPI.sendTitle(player, LanguageAPI.prepareRawMessage(player, "ignore_item_meta_enabled"),
                LanguageAPI.prepareRawMessage(player, "ignore_item_meta_inventory_can_edit"),
                10, 20*5, 10);
        Messages.create("ignore_item_meta_enabled")
                .FAILURE()
                .to(player)
                .handle();
    }

    public static void unsetIgnoreMeta(Player player){
        ignoreList.remove(player);
        Messages.create("ignore_item_meta_disabled")
                .to(player)
                .handle();
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
        IgnoreMetaCommand.setIgnoreMeta(player);
        return true;
    }

    @SubCommand("disable")
    public boolean disableIgnoreMeta(Player player) {
        IgnoreMetaCommand.unsetIgnoreMeta(player);
        return true;
    }
}
