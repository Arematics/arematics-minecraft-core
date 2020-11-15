package com.arematics.minecraft.core.messaging.injector;

import com.arematics.minecraft.core.language.LanguageAPI;
import com.arematics.minecraft.core.messaging.MessageHighlight;
import org.bukkit.command.CommandSender;

import java.util.List;

public class LanguageInjector extends BasicInjector {

    public LanguageInjector(List<CommandSender> senderList, MessageHighlight highlight,
                            String rawMessage) {
        super(senderList, highlight, rawMessage);
    }

    @Override
    public String prepareMessage(CommandSender sender) {
        if(!this.serverPrefix)
            return LanguageAPI.prepareRawMessage(sender, this.RAW_MESSAGE);
        return LanguageAPI.prepareMessage(sender, this.HIGHLIGHT, this.RAW_MESSAGE);
    }
}
