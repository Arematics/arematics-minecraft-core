package com.arematics.minecraft.core.messaging.advanced;

import com.arematics.minecraft.data.global.model.ChatClickAction;
import com.arematics.minecraft.data.global.model.ChatHoverAction;

public class PartBuilder {

    public static Part createHoverAndSuggest(String text, String hover, String suggestCommand) {
        return new Part(text)
                .setHoverAction(HoverAction.SHOW_TEXT, hover)
                .setClickAction(ClickAction.SUGGEST_COMMAND, suggestCommand);
    }

    public static Part createHoverAndRun(String text, String hover, String runCommand) {
        return new Part(text)
                .setHoverAction(HoverAction.SHOW_TEXT, hover)
                .setClickAction(ClickAction.RUN_COMMAND, runCommand);
    }

}
