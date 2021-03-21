package com.arematics.minecraft.core.command.processor.parser;

import com.arematics.minecraft.core.messaging.MessageInjector;
import com.arematics.minecraft.core.messaging.injector.Injector;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public class CommandProcessException extends RuntimeException {

    private final Function<MessageInjector, Injector<?>> injector;

    public CommandProcessException(String message){
        super(message);
        this.injector = null;
    }

    public CommandProcessException(String message, Function<MessageInjector, Injector<?>> injector){
        super(message);
        this.injector = injector;
    }
}
