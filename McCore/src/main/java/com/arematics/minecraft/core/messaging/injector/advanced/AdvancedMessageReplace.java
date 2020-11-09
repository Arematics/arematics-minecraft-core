package com.arematics.minecraft.core.messaging.injector.advanced;

import com.arematics.minecraft.core.messaging.advanced.MSG;
import com.arematics.minecraft.core.messaging.advanced.Part;

public interface AdvancedMessageReplace {
    AdvancedMessageReplace replace(String key, Part value);
    AdvancedMessageReplace replace(String key, MSG msg);
    AdvancedMessageReplace disableServerPrefix();
    void handle();
}
