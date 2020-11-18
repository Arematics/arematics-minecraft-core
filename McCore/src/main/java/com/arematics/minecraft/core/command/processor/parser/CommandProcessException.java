package com.arematics.minecraft.core.command.processor.parser;

import com.arematics.minecraft.core.messaging.injector.Injector;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommandProcessException extends RuntimeException {

    private final Injector<?> injector;

    public CommandProcessException(String message){
        super(message);
        this.injector = null;
    }
}
