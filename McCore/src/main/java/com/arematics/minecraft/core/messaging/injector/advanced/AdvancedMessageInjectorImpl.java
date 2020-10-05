package com.arematics.minecraft.core.messaging.injector.advanced;

import com.arematics.minecraft.core.language.Language;
import com.arematics.minecraft.core.language.LanguageAPI;
import com.arematics.minecraft.core.messaging.MessageHighlight;
import com.arematics.minecraft.core.messaging.advanced.*;
import com.arematics.minecraft.core.messaging.injector.Injector;
import com.google.common.collect.Streams;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.ObjectUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AdvancedMessageInjectorImpl extends Injector<MSG> implements AdvancedMessageReplace, AdvancedMessageAction {

    protected final List<CommandSender> SENDER_LIST;
    protected final MessageHighlight HIGHLIGHT;
    protected final String RAW_MESSAGE;
    protected final List<AdvancedReplace> INJECTOR_VALUES = new ArrayList<>();
    private boolean serverPrefix = true;

    protected AdvancedReplace current;

    public AdvancedMessageInjectorImpl(List<CommandSender> senderList, MessageHighlight highlight,
                                       String rawMessage) {
        this.SENDER_LIST = senderList;
        this.HIGHLIGHT = highlight;
        this.RAW_MESSAGE = rawMessage;
    }

    @Override
    public AdvancedMessageAction setHover(HoverAction action, String value){
        this.current.hoverAction = action;
        this.current.hoverValue = value;
        return this;
    }

    @Override
    public AdvancedMessageAction setClick(ClickAction action, String value) {
        this.current.clickAction = action;
        this.current.clickValue = value;
        return this;
    }

    @Override
    public AdvancedMessageAction setColor(JsonColor jsonColor) {
        this.current.jsonColor = jsonColor;
        return this;
    }

    @Override
    public AdvancedMessageAction setFormat(Format format) {
        this.current.format = format;
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
        this.current = new AdvancedReplace(pattern, new String[]{replace});
        return this;
    }

    @Override
    public AdvancedMessageAction eachReplace(String key, String[] values) {
        this.current = new AdvancedReplace(key, values);
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
        msg.replaceAllAt("%" + replace.key + "%", buildMessage(replace), false);
    }

    private MSG buildMessage(AdvancedReplace replace){
        MSG msg = new MSG();
        msg.PARTS.addAll(Streams.mapWithIndex(Arrays.stream(replace.values),
                (value, index) -> addPart(replace, index, replace.values.length - 1, value))
                .collect(Collectors.toList()));
        return msg;
    }

    private Part addPart(AdvancedReplace replace, long index, int maxLength, String value){
        return new Part(value + (index != maxLength ? ", " : ""))
                .setHoverAction(replace.hoverAction, injectPlaceholder(value, replace.hoverValue))
                .setClickAction(replace.clickAction, injectPlaceholder(value, replace.clickValue))
                .setBaseColor(replace.jsonColor)
                .addFormat(replace.format);
    }

    private String injectPlaceholder(String value, String rawMessage){
        Map<String, String> injectors = new HashMap<>();
        injectors.put("value", value);
        StrSubstitutor substitutor = new StrSubstitutor(injectors, "%", "%");
        return substitutor.replace(rawMessage);
    }
}
