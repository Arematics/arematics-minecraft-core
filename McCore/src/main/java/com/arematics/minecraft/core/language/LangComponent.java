package com.arematics.minecraft.core.language;

import com.arematics.minecraft.core.configurations.Config;
import com.arematics.minecraft.core.configurations.MessageHighlight;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Supplier;
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

    @Override
    public ComponentInject inject(String key, Supplier<String> t) {
        if(!injectors.containsKey(key))
            injectors.put(key, t);
        return this;
    }

    @Override
    public ComponentHighlight setMessageHighlight(MessageHighlight highlight) {
        this.highlight = highlight;
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
        Pattern pattern = Pattern.compile("%.*%");

        final String[] result = {income};

        if(pattern.matcher(income).find())
            injectors.forEach((key, value) -> result[0] = result[0].replaceAll(key, value.get()));
        return result[0];
    }
}
