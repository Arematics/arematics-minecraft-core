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

    public void addText(String key, String value){
        text.put(key, value);
    }

    public String getValue(String key){
        return text.getOrDefault(key, key);
    }
}
