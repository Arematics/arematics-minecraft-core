package com.arematics.minecraft.core.processor.methods.commands;

import org.bukkit.command.CommandSender;

public class CommandProcessorData {

    private final CommandSender sender;
    private final String[] commandArguments;
    private final String[] annotationValueArguments;

    public CommandProcessorData(CommandSender sender, String[] commandArguments, String[] annotationValueArguments){
        this.sender = sender;
        this.commandArguments = commandArguments;
        this.annotationValueArguments = annotationValueArguments;
    }

    public String[] commandInputArguments() {
        return commandArguments;
    }

    public String[] annotationValueArguments() {
        return annotationValueArguments;
    }
}
