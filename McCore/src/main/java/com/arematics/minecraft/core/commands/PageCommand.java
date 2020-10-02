package com.arematics.minecraft.core.commands;


import com.arematics.minecraft.core.annotations.Default;
import com.arematics.minecraft.core.annotations.PluginCommand;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.pages.Pageable;
import com.arematics.minecraft.core.pages.Pager;
import org.bukkit.command.CommandSender;

@PluginCommand(aliases = {"pager"})
public class PageCommand extends CoreCommand {

    public PageCommand() {
        super("page");
    }

    @Default
    public boolean sendInfo(CommandSender sender){
        Messages.create("cmd_not_valid")
                .to(sender)
                .setInjector(AdvancedMessageInjector.class)
                .replace("cmd_usage", "\n/page before\n/page next")
                .END()
                .handle();
        return true;
    }

    @SubCommand("{type}")
    public boolean listAll(CommandSender sender, PageType type){
        Pager pager = Pager.of(sender);
        Pageable pageable = pager.last();
        if(pageable == null) return true;
        switch (type){
            case BEFORE:
                if(pageable.hasBefore()) pageable.before();
                break;
            case NEXT:
                if(pageable.hasNext()) pageable.next();
                break;
        }
        return true;
    }

    public enum PageType{
        BEFORE(),
        NEXT();
    }
}
