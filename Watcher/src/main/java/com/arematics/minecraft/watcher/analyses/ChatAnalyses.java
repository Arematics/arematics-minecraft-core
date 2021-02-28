package com.arematics.minecraft.watcher.analyses;

import com.arematics.minecraft.data.service.ChatFilterService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class ChatAnalyses {

    private final ChatFilterService chatFilterService;

    private static final int MAX_DIFFERENCE = 1;
    private static final String HIDE_CHAR = "*";

    public boolean isBlocking(String message){
        return Arrays.stream(message.split(" "))
                .anyMatch(this::possibleBlocking);
    }

    private boolean possibleBlocking(String income){
        return chatFilterService.getBlocked().stream().anyMatch(block -> analyse(income, block));
    }

    private boolean analyse(String income, String blocked){
        return blocked.compareToIgnoreCase(income) == 0;
    }

    private boolean isMatchAfterReplaceHideChar(String income, String blocked){
        int result = blocked.compareToIgnoreCase(income);
        return result - 1 >= (StringUtils.countMatches(income, HIDE_CHAR) + MAX_DIFFERENCE)
                && result <= (StringUtils.countMatches(income, HIDE_CHAR) + MAX_DIFFERENCE);
    }

    private static boolean isDistanceMatch(String income, String blocked){
        int result = blocked.compareToIgnoreCase(income);
       return result - 1 >= MAX_DIFFERENCE && result <= MAX_DIFFERENCE;
    }
}
