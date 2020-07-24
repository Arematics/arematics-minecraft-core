package com.arematics.minecraft.core.messaging;

import com.arematics.minecraft.core.CoreEngine;
import com.arematics.minecraft.core.Engine;
import com.arematics.minecraft.core.configurations.Config;
import com.arematics.minecraft.core.messaging.injector.Injector;
import com.arematics.minecraft.core.messaging.injector.StringInjector;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message implements MessageHighlightType, MessageReciever, MessageInjector {

    static MessageHighlightType create(String key){
        return new Message(key);
    }

    private final String value;
    private MessageHighlight highlight;
    private Class<? extends Injector<?>> injectorType;
    private List<CommandSender> senders;

    private Message(String value){
        this.value = value;
        this.highlight = Config.getInstance().getHighlight(Config.SUCCESS);
        this.injectorType = Engine.getEngine(CoreEngine.class).getDefaultInjectorType();
    }

    @Override
    public MessageReciever WARNING() {
        this.highlight = Config.getInstance().getHighlight(Config.WARNING);
        return this;
    }

    @Override
    public MessageReciever FAILURE() {
        this.highlight = Config.getInstance().getHighlight(Config.FAILURE);
        return this;
    }

    @Override
    public MessageInjector to(CommandSender... senders){
        this.senders = Arrays.asList(senders);
        return this;
    }

    @Override
    public void handle() {
        DEFAULT().handle();
    }

    @Override
    public StringInjector DEFAULT() {
        return (StringInjector) setInjector(this.injectorType);
    }

    @Override
    public <T extends Injector<?>> T setInjector(Class<T> injector) {
        try{
            this.injectorType = injector;
            return injector.cast(this.injectorType.getConstructor(List.class, MessageHighlight.class,
                    String.class).newInstance(senders, highlight, value));
        }catch (Exception e){
            throw new RuntimeException("Could not construct Injector");
        }
    }

}
