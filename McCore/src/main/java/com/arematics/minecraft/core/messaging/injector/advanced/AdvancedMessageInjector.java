package com.arematics.minecraft.core.messaging.injector.advanced;

import com.arematics.minecraft.core.chat.model.ChatClickAction;
import com.arematics.minecraft.core.chat.model.ChatHoverAction;
import com.arematics.minecraft.core.chat.model.ReplacementData;
import com.arematics.minecraft.core.messaging.MessageHighlight;
import com.arematics.minecraft.core.messaging.advanced.MSG;
import com.arematics.minecraft.core.messaging.injector.Injector;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public class AdvancedMessageInjector extends Injector<MSG> implements AdvancedMessageReplace {

    private final AdvancedMessageInjectorImpl injector;
    public AdvancedMessageInjector(List<CommandSender> senderList, MessageHighlight highlight, String rawMessage) {
        this.injector = new AdvancedMessageInjectorImpl(senderList, highlight, rawMessage);
    }

    @Override
    public AdvancedMessageAction replace(String key, String value) {
        this.injector.replace(key, value);
        return injector;
    }

    @Override
    public AdvancedMessageAction eachReplace(String key, String[] values) {
        this.injector.eachReplace(key, values);
        return injector;
    }

    public AdvancedMessageAction replaceAllChatPlaceholders(List<ReplacementData> placeholders){
        placeholders.forEach(replacementData -> {
            AdvancedMessageAction action = injector.replace(replacementData.getKey(), replacementData.getValue());
            replacementData.getClick().forEach(chatClickAction -> action.setClick(chatClickAction.getAction(), chatClickAction.getValue()));
            replacementData.getHover().forEach(chatHoverAction -> action.setHover(chatHoverAction.getAction(), chatHoverAction.getValue()));
            action.END();
        });
        return injector;
    }

    public AdvancedMessageAction replaceAll(Map<String, String> keyValues) {
        keyValues.forEach((key, value) -> {
            AdvancedMessageAction action = injector.replace(key, value);
            action.END();
        });
        return injector;
    }

    @Override
    public AdvancedMessageReplace disableServerPrefix() {
        this.injector.disableServerPrefix();
        return this;
    }

    @Override
    public void handle() {
        this.injector.handle();
    }

    @Override
    protected String prepareMessage(CommandSender sender) {
        return this.injector.prepareMessage(sender);
    }

    @Override
    protected MSG injectValues(String income) {
        return this.injector.injectValues(income);
    }
}
