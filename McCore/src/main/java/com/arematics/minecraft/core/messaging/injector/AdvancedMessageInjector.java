package com.arematics.minecraft.core.messaging.injector;

import com.arematics.minecraft.core.configurations.Config;
import com.arematics.minecraft.core.language.LanguageAPI;
import com.arematics.minecraft.core.messaging.MessageHighlight;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.core.messaging.advanced.MSG;
import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvancedMessageInjector extends Injector<MSG> {

    protected final List<CommandSender> SENDER_LIST;
    protected final MessageHighlight HIGHLIGHT;
    protected final String RAW_MESSAGE;
    protected final Map<String, String> INJECTOR_VALUES = new HashMap<>();

    public AdvancedMessageInjector(List<CommandSender> senderList, MessageHighlight highlight,
                                   String rawMessage) {
        this.SENDER_LIST = senderList;
        this.HIGHLIGHT = highlight;
        this.RAW_MESSAGE = rawMessage;
    }

    public AdvancedMessageInjector addHover(HoverAction action, String value){
        return this;
    }

    public AdvancedMessageInjector replace(String pattern, String replace){
        this.INJECTOR_VALUES.put(pattern, replace);
        return this;
    }

    @Override
    public void handle() {
        SENDER_LIST.forEach(sender -> {
            String preparedMessage = prepareMessage(sender);
            MSG msg = injectValues(preparedMessage);
            msg.send(sender);
            if(sender instanceof Player)
                ((Player)sender).playSound(((Player)sender).getLocation(), this.HIGHLIGHT.getSound(), 1, 1);
        });
    }

    @Override
    protected String prepareMessage(CommandSender sender) {
        return LanguageAPI.prepareMessage(sender, this.HIGHLIGHT, this.RAW_MESSAGE);
    }

    @Override
    protected MSG injectValues(String income) {
        MSG msg = new MSG(income);
        this.INJECTOR_VALUES.forEach((key, value) -> msg.replaceAll("%" + key + "%", value));
        return msg;
    }
}
