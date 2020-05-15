package com.arematics.minecraft.core.processor.methods.commands;

import java.lang.reflect.Method;

public abstract class CommandAnnotationProcessor {

    private final CommandProcessorData commandProcessorData;

    public CommandAnnotationProcessor(CommandProcessorData commandProcessorData){
        this.commandProcessorData = commandProcessorData;
    }

    protected CommandProcessorData getProcessorData() {
        return commandProcessorData;
    }

    public abstract Object[] process(Method method);
}
