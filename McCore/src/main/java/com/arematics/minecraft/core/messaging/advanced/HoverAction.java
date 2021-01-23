package com.arematics.minecraft.core.messaging.advanced;

import com.google.gson.JsonParseException;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum HoverAction{

    SHOW_TEXT("show_text"),
    SHOW_ACHIEVEMENT("show_achievement"),
    SHOW_ITEM("show_item");

    public final String ACTION;

    @NonNull
    public static HoverAction findByAction(String name, HoverAction orElse) throws RuntimeException{
        HoverAction action = Arrays.stream(values()).filter(hoverAction -> hoverAction.ACTION.equals(name))
                .findFirst().orElse(orElse);
        if(action == null) throw new JsonParseException("No HoverAction found");
        return action;
    }

}
