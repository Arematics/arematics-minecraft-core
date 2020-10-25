package com.arematics.minecraft.core.chat.model;

import com.arematics.minecraft.core.data.model.message.ChatClickAction;
import com.arematics.minecraft.core.data.model.message.ChatHoverAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
public class GlobalPlaceholderActions {

    private String placeholderName;
    private ChatHoverAction hoverAction;
    private ChatClickAction clickAction;
}
