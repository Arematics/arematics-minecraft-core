package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.stereotype.Component;

@Component
public class OnlineTimeCommand extends CoreCommand {

    public OnlineTimeCommand(){
        super("onlinetime", true);
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        printOnlineTime(sender, sender);
    }

    @SubCommand("{player}")
    public void showOnlineTime(CorePlayer sender, CorePlayer target) {
        printOnlineTime(sender, target);
    }

    public void printOnlineTime(CorePlayer sender, CorePlayer target){
        target.onlineTime().updateOnlineTime();
        sender.info("Active Time Total: %time%")
                .DEFAULT()
                .replace("time", target.onlineTime().activeTimeString(false))
                .handle();
        sender.info("Active Time Current Server: %time%")
                .DEFAULT()
                .replace("time", target.onlineTime().activeTimeString(true))
                .handle();
    }
}
