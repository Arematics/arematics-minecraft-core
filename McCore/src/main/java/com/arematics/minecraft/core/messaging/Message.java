package com.arematics.minecraft.core.messaging;

import com.arematics.minecraft.core.configurations.Config;
import com.arematics.minecraft.core.configurations.MessageHighlight;
import com.arematics.minecraft.core.language.LanguageAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message implements MessageHighlightType, MessageReplacement {

    static MessageHighlightType create(String key){
        return new Message(key);
    }

    private final String value;
    private MessageHighlight highlight;

    private final HashMap<String, Supplier<String>> injectors = new HashMap<>();
    private final List<Supplier<String>> pointinjectors = new ArrayList<>();

    private Message(String value){
        this.value = value;
        this.highlight = Config.getInstance().getHighlight(Config.SUCCESS);
    };

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
    public MessageReplacement replace(String key, Supplier<String> supplier) {
        if(!injectors.containsKey(key))
            injectors.put(key, supplier);
        return this;
    }

    @Override
    public MessageReplacement replaceNext(Supplier<String> supplier) {
        if(!pointinjectors.contains(supplier))
            pointinjectors.add(supplier);
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
     * Override only this method if you want to change getting messages from the Language API
     * @param sender CommandSender
     * @param highlight MessageHighlight
     */
    private void handle(CommandSender sender, MessageHighlight highlight){
        String msg = injectValues(LanguageAPI.prepareMessage(sender, highlight, value));
        sender.sendMessage(msg);
        if(sender instanceof Player){
            ((Player)sender).playSound(((Player)sender).getLocation(), highlight.getSound(), 1, 1);
        }
    }

    private String injectValues(final String income){
        Pattern pattern = Pattern.compile("%.*%;?");
        Matcher matcher = pattern.matcher(income);

        final String[] result = {income};

        injectors.forEach((key, value) -> result[0] = result[0].replaceAll(key, value.get()));
        pointinjectors.forEach(value -> {
            if(matcher.find()) {
                String match = matcher.group();
                System.out.println(match);
                result[0] = result[0].replaceFirst(match, value.get());
            }
        });
        return result[0];
    }

    @Override
    public String toString(CommandSender sender) {
        return injectValues(LanguageAPI.prepareMessage(sender, highlight, value));
    }
}
