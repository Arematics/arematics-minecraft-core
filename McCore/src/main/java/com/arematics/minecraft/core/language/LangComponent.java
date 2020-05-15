package com.arematics.minecraft.core.language;

import com.arematics.minecraft.core.configurations.Config;
import com.arematics.minecraft.core.configurations.MessageHighlight;
import com.arematics.minecraft.core.server.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LangComponent implements ComponentInject, ComponentHighlight{

    private final String key;
    private MessageHighlight highlight;

    public static ComponentInject create(String key){
        return new LangComponent(key);
    }

    private LangComponent(String key){
        this.key = key;
        this.highlight = Config.getInstance().getHighlight(Config.SUCCESS);
    }

    private final HashMap<String, Supplier<String>> injectors = new HashMap<>();
    private final List<Supplier<String>> pointinjectors = new ArrayList<>();

    @Override
    public ComponentInject inject(String key, Supplier<String> t) {
        if(!injectors.containsKey(key))
            injectors.put(key, t);
        return this;
    }

    @Override
    public ComponentInject inject(Supplier<String> t) {
        if(!pointinjectors.contains(t))
            pointinjectors.add(t);
        return this;
    }

    @Override
    public ComponentHighlight setMessageHighlight(MessageHighlight highlight) {
        this.highlight = highlight;
        return this;
    }

    @Override
    public ComponentHighlight WARNING() {
        this.highlight = Config.getInstance().getHighlight(Config.WARNING);
        return this;
    }

    @Override
    public ComponentHighlight FAILURE() {
        this.highlight = Config.getInstance().getHighlight(Config.FAILURE);
        return this;
    }

    @Override
    public void broadcast() {
        send(Bukkit.getOnlinePlayers().toArray(new Player[]{}));
    }

    @Override
    public void send(Player... players) {
        Arrays.stream(players).forEach(player -> handle(player, this.highlight));
    }

    private void handle(Player p, MessageHighlight highlight){
       String msg = injectValues(LanguageAPI.prepareMessage(p, highlight, key));
       p.sendMessage(msg);
       p.playSound(p.getLocation(), highlight.getSound(), 1, 1);
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
}
