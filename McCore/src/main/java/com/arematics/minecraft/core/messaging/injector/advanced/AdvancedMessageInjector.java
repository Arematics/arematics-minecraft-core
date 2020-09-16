package com.arematics.minecraft.core.messaging.injector.advanced;

import com.arematics.minecraft.core.messaging.MessageHighlight;
import com.arematics.minecraft.core.messaging.advanced.MSG;
import com.arematics.minecraft.core.messaging.injector.Injector;
import org.bukkit.command.CommandSender;

import java.util.List;

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
