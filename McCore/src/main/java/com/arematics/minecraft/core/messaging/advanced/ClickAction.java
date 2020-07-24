package com.arematics.minecraft.core.messaging.advanced;

import com.google.gson.JsonParseException;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Optional;

public enum  ClickAction {

    OPEN_URL("open_url"),
    SUGGEST_COMMAND("suggest_command"),
    RUN_COMMAND("run_command");

    @NonNull
    public static ClickAction findByAction(String name, ClickAction orElse) throws RuntimeException {
        ClickAction action = Arrays.stream(values()).filter(clickAction -> clickAction.ACTION.equals(name))
                .findFirst().orElse(orElse);
        if(action == null) throw new JsonParseException("No ClickAction found");
        return action;
    }

    public final String ACTION;

    ClickAction(String action){
        this.ACTION = action;
    }
}
