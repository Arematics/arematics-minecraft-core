package com.arematics.minecraft.core.messaging.injector.advanced;

import com.arematics.minecraft.core.language.LanguageAPI;
import com.arematics.minecraft.core.messaging.MessageHighlight;
import com.arematics.minecraft.core.messaging.advanced.MSG;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.injector.Injector;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class AdvancedMessageInjectorImpl extends Injector<MSG> implements AdvancedMessageReplace {

    protected final List<CommandSender> SENDER_LIST;
    protected final MessageHighlight HIGHLIGHT;
    protected final String RAW_MESSAGE;
    protected final Map<String, MSG> INJECTOR_VALUES = new HashMap<>();
    private boolean serverPrefix = true;

    public AdvancedMessageInjectorImpl(List<CommandSender> senderList, MessageHighlight highlight,
                                       String rawMessage) {
        this.SENDER_LIST = senderList;
        this.HIGHLIGHT = highlight;
        this.RAW_MESSAGE = rawMessage;
    }

    @Override
    public AdvancedMessageReplace replace(String pattern, Part replace){
        this.INJECTOR_VALUES.put(pattern, new MSG(replace));
        return this;
    }

    @Override
    public AdvancedMessageReplace replace(String key, MSG msg) {
        this.INJECTOR_VALUES.put(key, msg);
        return this;
    }

    @Override
    public AdvancedMessageReplace disableServerPrefix() {
        this.serverPrefix = false;
        return this;
    }

    @Override
    public void handle() {
        SENDER_LIST.forEach(sender -> {
            String preparedMessage = prepareMessage(sender);
            MSG msg = injectValues(preparedMessage);
            msg.send(sender);
            if(!this.HIGHLIGHT.getColorCode().equals("§a") && sender instanceof Player)
                ((Player)sender).playSound(((Player)sender).getLocation(), this.HIGHLIGHT.getSound(), 1, 1);
        });
    }

    @Override
    protected String prepareMessage(CommandSender sender) {
        if(!this.serverPrefix)
            return LanguageAPI.prepareRawMessage(sender, this.RAW_MESSAGE);
        return LanguageAPI.prepareMessage(sender, this.HIGHLIGHT, this.RAW_MESSAGE);
    }

    @Override
    protected MSG injectValues(String income) {
        MSG msg = new MSG(income);
        this.INJECTOR_VALUES.forEach((key, value) -> msg.replaceAllAt("%" + key + "%", value, false));
        return msg;
    }

    @Override
    public MSG toObject(CommandSender sender) {
        String preparedMessage = prepareMessage(sender);
        return injectValues(preparedMessage);
    }

    @Override
    public MSG toObjectForFirst() {
        return toObject(SENDER_LIST.get(0));
    }
}
