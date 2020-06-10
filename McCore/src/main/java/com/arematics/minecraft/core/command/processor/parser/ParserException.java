package com.arematics.minecraft.core.command.processor.parser;

import com.arematics.minecraft.core.messaging.MessageReplacement;
import org.bukkit.command.CommandSender;

public class ParserException extends Exception {

    public ParserException(MessageReplacement error, CommandSender sender){
        super(error.toString(sender));
    }

    public ParserException(String message){
        super(message);
    }
}
