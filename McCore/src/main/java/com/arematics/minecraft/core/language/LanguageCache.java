package com.arematics.minecraft.core.language;

import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LanguageCache {

    private static final LanguageCache instance;

    static{
        instance = new LanguageCache();
    }

    public static LanguageCache getInstance() {
        return instance;
    }

    private final Map<String, Language> languages = new HashMap<>();

    public void registerMessage(String rawLanguageName, String key, String message){
        if(!languages.containsKey(rawLanguageName))
            languages.put(rawLanguageName, new Language(rawLanguageName));
        Language language = languages.get(rawLanguageName);
        language.addText(key, message);
    }

    public void registerFile(InputStream stream){
        Properties properties = new Properties();

        try{
            properties.load(new InputStreamReader(stream, StandardCharsets.UTF_8));
            String langName = properties.getProperty("language_name").replaceAll("\"", "");
            properties.forEach((k, s) -> addLanguageKeyValue(langName, k.toString(), s.toString().replaceAll("\"", "")));
        }catch (IOException ioe){
            Bukkit.getLogger().severe("Could not load File " + ioe.getMessage());
        }

    }

    private void addLanguageKeyValue(String langName, String key, String value){
        if(!key.equals("language_name"))  registerMessage(langName, key, value);
    }
}
