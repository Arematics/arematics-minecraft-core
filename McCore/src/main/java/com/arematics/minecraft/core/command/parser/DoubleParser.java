package com.arematics.minecraft.core.command.parser;

public class DoubleParser extends CommandParameterParser<Double>{
    @Override
    public Double doParse(String value) throws ParserException {
        try{
            return Double.parseDouble(value);
        }catch (NumberFormatException e){
            throw new ParserException(value + " must be a number");
        }
    }
}
