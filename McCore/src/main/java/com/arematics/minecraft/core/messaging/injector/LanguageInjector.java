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
        return LanguageAPI.prepareMessage(sender, super.HIGHLIGHT, super.RAW_MESSAGE);
    }
}
