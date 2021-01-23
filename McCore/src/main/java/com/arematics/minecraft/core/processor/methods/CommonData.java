package com.arematics.minecraft.core.processor.methods;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public enum CommonData {

    COMMAND("command"),
    COMMAND_SENDER("sender"),
    COMMAND_ARGUMENTS("arguments");

    private final String key;
}
