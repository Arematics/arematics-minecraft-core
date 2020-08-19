package com.arematics.minecraft.core.messaging.injector.advanced;

import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.Format;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.core.messaging.advanced.JsonColor;

public class AdvancedReplace {

    public final String KEY;
    public final String VALUE;
    public HoverAction HOVER_ACTION;
    public String HOVER_VALUE;
    public ClickAction CLICK_ACTION;
    public String CLICK_VALUE;
    public JsonColor JSON_COLOR;
    public Format FORMAT;

    public AdvancedReplace(String key, String value){
        this.KEY = key;
        this.VALUE = value;
    }
}
