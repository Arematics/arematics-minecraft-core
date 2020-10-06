package com.arematics.minecraft.core.chat.model;

import com.arematics.minecraft.core.data.model.message.ChatClickAction;
import com.arematics.minecraft.core.data.model.message.ChatHoverAction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlaceholderActionInput {

    private String placeholderName;
    private ChatHoverAction hoverAction;
    private ChatClickAction clickAction;
}
