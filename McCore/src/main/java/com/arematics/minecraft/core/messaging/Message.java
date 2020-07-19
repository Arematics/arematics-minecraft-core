package com.arematics.minecraft.core.messaging;

import com.arematics.minecraft.core.configurations.Config;
import com.arematics.minecraft.core.language.LanguageAPI;
import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;

public class Message implements MessageHighlightType, MessageReplacement {

    static MessageHighlightType create(String key){
        return new Message(key);
    }

    private final String value;
    private MessageHighlight highlight;

    private final HashMap<String, String> injectors = new HashMap<>();

    private Message(String value){
        this.value = value;
        this.highlight = Config.getInstance().getHighlight(Config.SUCCESS);
    }

    @Override
    public MessageReplacement WARNING() {
        this.highlight = Config.getInstance().getHighlight(Config.WARNING);
        return this;
    }

    @Override
    public MessageReplacement FAILURE() {
        this.highlight = Config.getInstance().getHighlight(Config.FAILURE);
        return this;
    }

    @Override
    public MessageReplacement replace(String key, String value) {
        if(!injectors.containsKey(key))
            injectors.put(key, value);
        return this;
    }

    @Override
    public MessageReplacement skip() {
        return this;
    }

    @Override
    public void send(CommandSender... senders) {
        Arrays.stream(senders).forEach(sender -> handle(sender, this.highlight));
    }

    @Override
    public void broadcast() {
        send(Bukkit.getOnlinePlayers().toArray(new Player[]{}));
    }

    /**
     * Change only this method if you want to change where prepare messages from
     * @param sender CommandSender for getting specific Messages like Language Selection
     * @param highlight Message Highlight Type
     * @param value Raw Message String
     */
    private String prepareMessage(CommandSender sender, MessageHighlight highlight, String value){
        return LanguageAPI.prepareMessage(sender, highlight, value);
    }

    private void handle(CommandSender sender, MessageHighlight highlight){
        String msg = injectValues(prepareMessage(sender, highlight, value));
        sender.sendMessage(msg);
        if(sender instanceof Player){
            ((Player)sender).playSound(((Player)sender).getLocation(), highlight.getSound(), 1, 1);
        }
    }

    private String injectValues(final String income){
        StrSubstitutor substitutor = new StrSubstitutor(injectors, "%", "%");
        return substitutor.replace(income);
    }

    @Override
    public String toString(CommandSender sender) {
        return injectValues(prepareMessage(sender, highlight, value));
    }
}
