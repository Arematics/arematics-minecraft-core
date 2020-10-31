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
import org.springframework.stereotype.Component;

@Component
@PluginCommand(aliases = {"pager"})
public class PageCommand extends CoreCommand {

    public PageCommand() {
        super("page");
    }

    @Default
    @Override
    public boolean onDefaultExecute(CommandSender sender){
        Messages.create("cmd_not_valid")
                .to(sender)
                .setInjector(AdvancedMessageInjector.class)
                .replace("cmd_usage", "\n/page before\n/page next")
                .END()
                .handle();
        return true;
    }

    @SubCommand("{type}")
    public boolean page(CommandSender sender, PageType type){
        return pageFor(sender, type, null);
    }

    @SubCommand("{type} {key}")
    public boolean pageFor(CommandSender sender, PageType type, String key){
        Pager pager = Pager.of(sender);
        Pageable pageable = key == null ? pager.last() : pager.fetch(key);
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
