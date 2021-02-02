package com.arematics.minecraft.core.utils;

public class CommandUtils {

    public static String prettyReplace(String key, String keyValue){
        return "§a\n\n§7" + key + " » " + "§c" + keyValue + "\n" + "%value%";
    }

    public static String prettyHeader(String key, String value){
        return "§a\n\n§7" + key + " » " + "§c" + value;
    }

    public static String prettyBoolean(boolean value){
        return value ? "Yes" : "No";
    }
}
