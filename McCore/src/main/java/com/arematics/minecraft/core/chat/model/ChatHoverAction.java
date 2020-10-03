package com.arematics.minecraft.core.chat.model;

import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChatHoverAction {
    private HoverAction action;
    private String value;
}
