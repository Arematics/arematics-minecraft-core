package com.arematics.minecraft.core.processor.methods;

public enum CommonData {

    COMMAND("cmd"),
    COMMAND_SENDER("cmd_sender"),
    COMMAND_ARGUEMNTS("cmd_args");

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
