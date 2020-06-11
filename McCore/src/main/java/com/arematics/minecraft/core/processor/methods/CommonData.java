package com.arematics.minecraft.core.processor.methods;

public enum CommonData {

    COMMAND("command"),
    COMMAND_SENDER("sender"),
    COMMAND_ARGUEMNTS("arguments");

    private final String key;

    CommonData(String key){
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return key;
    }
}
