package com.arematics.minecraft.core.language;

import com.arematics.minecraft.core.configurations.Config;
import com.arematics.minecraft.core.messaging.MessageHighlight;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class LanguageAPI {

    private static final Map<String, Language> langs = new HashMap<>();
    private static final Map<Player, LanguageUser> users = new HashMap<>();

    public static LanguageUser getUser(Player p){
        if(!users.containsKey(p)) {
            LanguageUser user = new LanguageUser(p);
            user.setLanguage(langs.get("ENGLISH"));
            users.put(p, user);
        }

        return users.get(p);
    }

    public static Language getLanguage(String key){
        return langs.get(key);
    }

    public static String prepareMessage(CommandSender sender, MessageHighlight highlight, String key){
        if(sender instanceof Player){
            return Config.getInstance().getPrefix() +
                    highlight.getColorCode() +
                    getUser((Player)sender).getLanguage().getValue(key);
        }

        return Config.getInstance().getPrefix() +
                highlight.getColorCode() +
                langs.get("ENGLISH").getValue(key);
    }

    public static String prepareRawMessage(CommandSender sender, String key){
        if(sender instanceof Player){
            return getUser((Player)sender).getLanguage().getValue(key);
        }

        return langs.get("ENGLISH").getValue(key);
    }

    public static void registerMessage(String langName, String key, String message){
        Optional<Language> lang = langs.values().stream().filter(language -> language.getName().equals(langName))
                .findFirst();
        if(!lang.isPresent()) generateLanguage(langName).addText(key, message);
        else lang.get().addText(key, message.replaceAll("&", "§"));
    }

    private static Language generateLanguage(String name){
        Language lang = new Language(name);
        langs.put(name, lang);
        return lang;
    }

    public static boolean registerFile(BufferedReader reader){
        Properties properties = new Properties();

        try{
            properties.load(reader);
            String langName = properties.getProperty("language_name").replaceAll("\"", "");
            properties.forEach((k, s) -> addVals(langName, k.toString(), s.toString().replaceAll("\"", "")));
        }catch (IOException ioe){
            Bukkit.getLogger().severe("Could not load File " + ioe.getMessage());
        }

        return false;
    }

    private static void addVals(String langName, String key, String value){
        if(!key.equals("language_name"))  registerMessage(langName, key, value);
    }
}
