package com.arematics.minecraft.core.language;

import java.util.HashMap;
import java.util.Map;

public class Language {

    private final String name;
    private final Map<String, String> text = new HashMap<>();

    public Language(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String addText(String key, String value){
        if(text.containsKey(key))
            return text.get(key);
        else
            text.put(key, value);
        return value;
    }

    public String getValue(String key){
        return text.getOrDefault(key, null);
    }
}
