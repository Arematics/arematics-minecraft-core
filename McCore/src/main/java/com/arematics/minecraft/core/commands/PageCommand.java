package com.arematics.minecraft.core.commands;


import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.stereotype.Component;

@Component
public class PageCommand extends CoreCommand {

    public PageCommand() {
        super("page", "pager");
    }

    @Override
    public void onDefaultExecute(CorePlayer sender){
        sender.info("cmd_not_valid")
                .setInjector(AdvancedMessageInjector.class)
                .replace("cmd_usage", new Part("\n/page BEFORE\n/page NEXT"))
                .handle();
    }

    @SubCommand("{type} {message}")
    public void page(CorePlayer player, PageType type, String message){
        if (type == PageType.BEFORE) {
            player.inventories().pageBefore();
        }else player.inventories().nextPage();
        player.interact().dispatchCommand(message);
    }

    public enum PageType{
        BEFORE(),
        NEXT();
    }
}
