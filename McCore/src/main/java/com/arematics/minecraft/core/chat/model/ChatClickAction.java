package com.arematics.minecraft.core.chat.model;

import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChatClickAction {
    private ClickAction action;
    private String value;
}
