package com.arematics.minecraft.core.messaging.injector.advanced;

public interface AdvancedMessageReplace {
    AdvancedMessageAction replace(String key, String value);
    AdvancedMessageAction eachReplace(String key, String[] values);
    AdvancedMessageReplace disableServerPrefix();
    void handle();
}