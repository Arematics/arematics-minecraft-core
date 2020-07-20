package com.arematics.minecraft.watcher.analyses;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ChatAnalyses {

    private static final List<String> blocked = new ArrayList<>();
    private static final int MAX_DIFFERENCE = 2;
    private static final String HIDE_CHAR = "*";

    static{
        blocked.add("Arschloch");
    }

    public static String[] doChatMessageCheck(String message){
        List<String> messages = new ArrayList<>();
        for(String value : message.split(" ")){
            String result = checkString(value);
            if(result != null) messages.add(result);
        }
        return messages.toArray(new String[]{});
    }

    private static String checkString(String income){
        if(possibleBlocking(income))
            return income;
        return null;
    }

    private static boolean possibleBlocking(String income){
        return blocked.stream().anyMatch(block -> analyse(income, block));
    }

    private static boolean analyse(String income, String blocked){
        return isDistanceMatch(income, blocked) || isMatchAfterReplaceHideChar(income, blocked);
    }

    private static boolean isMatchAfterReplaceHideChar(String income, String blocked){
        return StringUtils.getLevenshteinDistance(blocked, income) <=
                StringUtils.countMatches(income, HIDE_CHAR) + MAX_DIFFERENCE &&
                isLengthMatch(income, blocked);
    }

    private static boolean isDistanceMatch(String income, String blocked){
       return StringUtils.getLevenshteinDistance(blocked, income) <= MAX_DIFFERENCE &&
                isLengthMatch(income, blocked);
    }

    private static boolean isLengthMatch(String income, String blocked){
        return income.length() == blocked.length() ||
                income.length() + 1 == blocked.length() ||
                income.length() - 1 == blocked.length();
    }
}
