package com.arematics.minecraft.core.command.processor.validator;

import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PositiveNumberValidator extends ParameterValidator<Number> {

    @Override
    public void supply(Number result, List<Object> data) throws CommandProcessException {
        if(result.intValue() <= 0) throw new CommandProcessException("Only positive numbers allowed here");
    }
}
