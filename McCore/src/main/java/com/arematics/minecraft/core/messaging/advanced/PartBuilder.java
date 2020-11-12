package com.arematics.minecraft.core.messaging.advanced;

public class PartBuilder {

    public static Part createHoverAndSuggest(String text, String hover, String suggestCommand){
        return new Part(text)
                .setHoverAction(HoverAction.SHOW_TEXT, hover)
                .setClickAction(ClickAction.RUN_COMMAND, suggestCommand);
    }
}
