package com.arematics.minecraft.core.messaging.injector.advanced;

import com.arematics.minecraft.core.language.LanguageAPI;
import com.arematics.minecraft.core.messaging.MessageHighlight;
import com.arematics.minecraft.core.messaging.advanced.MSG;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.injector.Injector;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdvancedMessageInjectorImpl extends Injector<MSG> implements AdvancedMessageReplace {

    protected final List<CommandSender> SENDER_LIST;
    protected final MessageHighlight HIGHLIGHT;
    protected final String RAW_MESSAGE;
    protected final List<AdvancedReplace> INJECTOR_VALUES = new ArrayList<>();
    private boolean serverPrefix = true;

    public AdvancedMessageInjectorImpl(List<CommandSender> senderList, MessageHighlight highlight,
                                       String rawMessage) {
        this.SENDER_LIST = senderList;
        this.HIGHLIGHT = highlight;
        this.RAW_MESSAGE = rawMessage;
    }

    @Override
    public AdvancedMessageReplace replace(String pattern, Part replace){
        this.INJECTOR_VALUES.add(new AdvancedReplace(pattern, new Part[]{replace}));
        return this;
    }

    @Override
    public AdvancedMessageReplace eachReplace(String key, Part[] values) {
        this.INJECTOR_VALUES.add(new AdvancedReplace(key, values));
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
            if(sender instanceof Player)
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
        this.INJECTOR_VALUES.forEach(replace -> handleReplacer(replace, msg));
        return msg;
    }

    private void handleReplacer(AdvancedReplace replace, MSG msg){
        msg.replaceAllAt("%" + replace.key + "%", new MSG(Arrays.asList(replace.parts)), false);
    }
}
