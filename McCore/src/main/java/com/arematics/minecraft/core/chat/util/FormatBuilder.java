package com.arematics.minecraft.core.chat.util;

import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FormatBuilder {

    private final List<String> format = new ArrayList<>();

    public static FormatBuilder create() {
        return new FormatBuilder();
    }

    public FormatBuilder add(String format) {
       getFormat().add(format);
       return this;
    }

    public FormatBuilder space() {
        getFormat().add(" ");
        return this;
    }

    public String build(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getFormat().size(); i++) {
            sb.append(getFormat().get(i)).append(",");
        }
        return sb.toString();
    }

}
