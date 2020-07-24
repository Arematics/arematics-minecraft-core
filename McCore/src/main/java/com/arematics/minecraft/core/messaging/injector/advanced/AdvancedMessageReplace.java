package com.arematics.minecraft.core.messaging.injector.advanced;

public interface AdvancedMessageReplace {
    AdvancedMessageAction replace(String key, String value);
    void handle();
}
