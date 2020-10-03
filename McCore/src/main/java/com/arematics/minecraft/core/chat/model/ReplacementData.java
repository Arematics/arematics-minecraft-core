package com.arematics.minecraft.core.chat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReplacementData {

    private String key;
    private String value;
    private List<ChatClickAction> click = new ArrayList<>();
    private List<ChatHoverAction> hover = new ArrayList<>();
}
