package com.arematics.minecraft.data.global.model;

public interface PlaceholderAction {
    String getPlaceholderKey();
    ChatHoverAction getHoverAction();
    ChatClickAction getClickAction();
}
