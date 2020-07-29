package com.arematics.minecraft.core.messaging.injector.advanced;

import com.arematics.minecraft.core.language.LanguageAPI;
import com.arematics.minecraft.core.messaging.MessageHighlight;
import com.arematics.minecraft.core.messaging.advanced.*;
import com.arematics.minecraft.core.messaging.injector.Injector;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdvancedMessageInjectorImpl extends Injector<MSG> implements AdvancedMessageReplace, AdvancedMessageAction {

    protected final List<CommandSender> SENDER_LIST;
    protected final MessageHighlight HIGHLIGHT;
    protected final String RAW_MESSAGE;
    protected final List<AdvancedReplace> INJECTOR_VALUES = new ArrayList<>();

    protected AdvancedReplace current;

    public AdvancedMessageInjectorImpl(List<CommandSender> senderList, MessageHighlight highlight,
                                       String rawMessage) {
        this.SENDER_LIST = senderList;
        this.HIGHLIGHT = highlight;
        this.RAW_MESSAGE = rawMessage;
    }

    @Override
    public AdvancedMessageAction setHover(HoverAction action, String value){
        this.current.HOVER_ACTION = action;
        this.current.HOVER_VALUE = value;
        return this;
    }

    @Override
    public AdvancedMessageAction setClick(ClickAction action, String value) {
        this.current.CLICK_ACTION = action;
        this.current.CLICK_VALUE = value;
        return this;
    }

    @Override
    public AdvancedMessageAction setColor(JsonColor jsonColor) {
        this.current.JSON_COLOR = jsonColor;
        return this;
    }

    @Override
    public AdvancedMessageAction setFormat(Format format) {
        this.current.FORMAT = format;
        return this;
    }

    @Override
    public AdvancedMessageReplace END() {
        if(current != null)
            this.INJECTOR_VALUES.add(current);
        this.current = null;
        return this;
    }

    @Override
    public AdvancedMessageAction replace(String pattern, String replace){
        this.current = new AdvancedReplace(pattern, replace);
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
        this.INJECTOR_VALUES.forEach(replace -> handleReplacer(replace, msg));
        return msg;
    }

    private void handleReplacer(AdvancedReplace replace, MSG msg){
        Part[] parts = msg.separateTerms("%" + replace.KEY + "%");
        Arrays.stream(parts).forEach(part -> part
                .setText(replace.VALUE)
                .setHoverAction(replace.HOVER_ACTION, replace.HOVER_VALUE)
                .setClickAction(replace.CLICK_ACTION, replace.CLICK_VALUE)
                .setBaseColor(replace.JSON_COLOR)
                .addFormat(replace.FORMAT));
    }
}
