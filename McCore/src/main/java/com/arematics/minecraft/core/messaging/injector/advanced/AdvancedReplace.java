package com.arematics.minecraft.core.messaging.injector.advanced;

import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.Format;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.core.messaging.advanced.JsonColor;

public class AdvancedReplace {

    public final String key;
    public final String[] values;
    public HoverAction hoverAction;
    public String hoverValue;
    public ClickAction clickAction;
    public String clickValue;
    public JsonColor jsonColor;
    public Format format;

    public AdvancedReplace(String key, String[] values){
        this.key = key;
        this.values = values;
    }
}
