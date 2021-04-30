package com.arematics.minecraft.core.messaging.injector.advanced;

import com.arematics.minecraft.core.messaging.MessageHighlight;
import com.arematics.minecraft.core.messaging.advanced.MSG;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.injector.Injector;
import org.bukkit.command.CommandSender;

import java.util.List;

public class AdvancedMessageInjector extends Injector<MSG> implements AdvancedMessageReplace {

    private final AdvancedMessageInjectorImpl injector;

    public AdvancedMessageInjector(List<CommandSender> senderList, MessageHighlight highlight, String rawMessage) {
        this.injector = new AdvancedMessageInjectorImpl(senderList, highlight, rawMessage);
    }

    @Override
    public AdvancedMessageReplace replace(String key, Part value) {
        this.injector.replace(key, value);
        return injector;
    }

    @Override
    public AdvancedMessageReplace replace(String key, MSG msg) {
        this.injector.replace(key, msg);
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

    @Override
    public MSG toObject(CommandSender sender) {
        return injector.toObject(sender);
    }

    @Override
    public MSG toObjectForFirst() {
        return injector.toObjectForFirst();
    }
}
