package com.arematics.minecraft.core.messaging.advanced;

import com.google.gson.JsonParseException;
import lombok.NonNull;

import java.util.Arrays;

public enum HoverAction{

    SHOW_TEXT("show_text"),
    SHOW_ACHIEVEMENT("show_achievement"),
    SHOW_ITEM("show_item");

    @NonNull
    public static HoverAction findByAction(String name, HoverAction orElse) throws RuntimeException{
        HoverAction action = Arrays.stream(values()).filter(hoverAction -> hoverAction.ACTION.equals(name))
                .findFirst().orElse(orElse);
        if(action == null) throw new JsonParseException("No HoverAction found");
        return action;
    }

    public final String ACTION;

    HoverAction(String action){
        this.ACTION = action;
    }
}
