package com.arematics.minecraft.core.chat.util;

import com.arematics.minecraft.data.global.model.GlobalPlaceholderAction;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class GlobalActionsBuilder {

    private final Set<GlobalPlaceholderAction> globals = new HashSet<>();

    public static GlobalActionsBuilder create() {
        return new GlobalActionsBuilder();
    }

    public GlobalActionsBuilder add(GlobalPlaceholderAction action) {
        getGlobals().add(action);
        return this;
    }

    public Set<GlobalPlaceholderAction> build() {
        return getGlobals();
    }

}
