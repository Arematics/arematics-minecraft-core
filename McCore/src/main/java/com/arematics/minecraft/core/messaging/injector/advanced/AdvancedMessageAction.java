package com.arematics.minecraft.core.messaging.injector.advanced;

import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.Format;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.core.messaging.advanced.JsonColor;

public interface AdvancedMessageAction {
    AdvancedMessageAction setHover(HoverAction action, String value);
    AdvancedMessageAction setClick(ClickAction action, String value);
    AdvancedMessageAction setColor(JsonColor jsonColor);
    AdvancedMessageAction setFormat(Format format);
    AdvancedMessageReplace END();
}
