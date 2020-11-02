package com.arematics.minecraft.core.messaging.injector.advanced;

import com.arematics.minecraft.core.messaging.advanced.Part;

public interface AdvancedMessageReplace {
    AdvancedMessageReplace replace(String key, Part value);
    AdvancedMessageReplace eachReplace(String key, Part[] values);
    AdvancedMessageReplace disableServerPrefix();
    void handle();
}
