package com.arematics.minecraft.core.messaging.injector.advanced;

import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;

public interface AdvancedMessageAction {
    AdvancedMessageAction setHover(HoverAction action, String value);
    AdvancedMessageAction setClick(ClickAction action, String value);
    AdvancedMessageReplace END();
}
