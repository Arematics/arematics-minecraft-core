package com.arematics.minecraft.core.command.processor;

import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;

public interface Processor {

    Object[] process(Object[] input, String[] annotationValues, CommandSender sender, Method method)
            throws com.arematics.minecraft.core.command.processor.ProcessorException, CommandProcessException;
}
