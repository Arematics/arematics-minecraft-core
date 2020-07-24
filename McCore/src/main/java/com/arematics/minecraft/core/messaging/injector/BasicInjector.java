package com.arematics.minecraft.core.messaging.injector;

import com.arematics.minecraft.core.configurations.Config;
import com.arematics.minecraft.core.messaging.MessageHighlight;
import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicInjector extends StringInjector {

    protected final List<CommandSender> SENDER_LIST;
    protected final MessageHighlight HIGHLIGHT;
    protected final String RAW_MESSAGE;
    protected final Map<String, String> INJECTOR_VALUES = new HashMap<>();

    public BasicInjector(List<CommandSender> senderList, MessageHighlight highlight,
                         String rawMessage){
        this.SENDER_LIST = senderList;
        this.HIGHLIGHT = highlight;
        this.RAW_MESSAGE = rawMessage;
    }

    @Override
    public BasicInjector replace(String pattern, String replace){
        this.INJECTOR_VALUES.put(pattern, replace);
        return this;
    }

    @Override
    protected String prepareMessage(CommandSender sender) {
        return Config.getInstance().getPrefix() + this.HIGHLIGHT.getColorCode() + this.RAW_MESSAGE;
    }

    @Override
    protected String injectValues(String income) {
        StrSubstitutor substitutor = new StrSubstitutor(this.INJECTOR_VALUES, "%", "%");
        return substitutor.replace(income);
    }

    @Override
    public void handle() {
        SENDER_LIST.forEach(sender -> {
            String preparedMessage = prepareMessage(sender);
            String msg = injectValues(preparedMessage);
            sender.sendMessage(msg);
            if(sender instanceof Player)
                ((Player)sender).playSound(((Player)sender).getLocation(), this.HIGHLIGHT.getSound(), 1, 1);
        });
    }
}
