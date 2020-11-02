package com.arematics.minecraft.core.messaging.injector.advanced;

import com.arematics.minecraft.core.messaging.advanced.Part;

public class AdvancedReplace {

    public final String key;
    public final Part[] parts;

    public AdvancedReplace(String key, Part[] parts){
        this.key = key;
        this.parts = parts;
    }
}
