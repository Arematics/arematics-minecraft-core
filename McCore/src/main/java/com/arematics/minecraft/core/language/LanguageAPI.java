package com.arematics.minecraft.core.language;

import com.arematics.minecraft.core.configurations.Config;
import com.arematics.minecraft.core.configurations.MessageHighlight;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LanguageAPI {

    private static final Map<String, Language> langs = new HashMap<>();
    private static final Map<Player, LanguageUser> users = new HashMap<>();

    static LanguageUser getUser(Player p){
        if(!users.containsKey(p)) {
            LanguageUser user = new LanguageUser(p);
            user.setLanguage(langs.get("ENGLISH"));
            users.put(p, user);
        }

        return users.get(p);
    }

    public static void broadcast(String key){
        for(LanguageUser user : users.values()){
            user.getPlayer().sendMessage(user.getLanguage().getValue(key));
        }
    }

    public static ComponentInject injectable(String key){
        return LangComponent.create(key);
    }

    static String prepareMessage(Player player, MessageHighlight highlight, String key){
        checkExistsAndAddIfNot(player);
        return Config.getInstance().getPrefix() +
                highlight.getColorCode() +
                users.get(player).getLanguage().getValue(key);
    }

    private static void sendMessage(Player player, MessageHighlight highlight, String key){
        checkExistsAndAddIfNot(player);
        player.sendMessage(prepareMessage(player, highlight, key));
        player.playSound(player.getLocation(), highlight.getSound(), 1, 1);
    }

    public static void sendMessage(Player player, String key){
        sendMessage(player, Config.getInstance().getHighlight(Config.SUCCESS), key);
    }

    public static void sendWarning(Player player, String key){
        sendMessage(player, Config.getInstance().getHighlight(Config.WARNING), key);
    }

    public static void sendFailure(Player player, String key){
        sendMessage(player, Config.getInstance().getHighlight(Config.FAILURE), key);
    }

    private static void checkExistsAndAddIfNot(Player player){
        if(users.get(player) == null){
            LanguageUser user = new LanguageUser(player);
            user.setLanguage(langs.get("ENGLISH"));
            users.put(player, user);
        }
    }

    public static void registerMessage(String langName, String key, String message){
        Language lang = langs.values().stream().filter(language -> language.getName().equals(langName))
                .findFirst().orElse(generateLanguage(langName));
        lang.addText(key, message);
    }

    private static Language generateLanguage(String name){
        Language lang = new Language(name);
        langs.put(name, lang);
        return lang;
    }

    public static boolean registerFile(InputStream stream){
        Properties properties = new Properties();

        try{
            properties.load(new InputStreamReader(stream, StandardCharsets.UTF_8));
            String langName = properties.getProperty("language_name");
            properties.forEach((k, s) -> addVals(langName, k.toString(), s.toString()));
        }catch (IOException ioe){
            Bukkit.getLogger().severe("Could not load File " + ioe.getMessage());
        }

        return false;
    }

    private static void addVals(String langName, String key, String value){
        if(!key.equals("language_name"))  registerMessage(langName, key, value);
    }
}
