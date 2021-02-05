package com.arematics.minecraft.core.command.processor.parser;

import org.bukkit.potion.PotionEffectType;
import org.springframework.stereotype.Component;

@Component
public class PotionEffectTypeParser extends CommandParameterParser<PotionEffectType> {
    @Override
    public PotionEffectType parse(String value) throws CommandProcessException {
        PotionEffectType type = PotionEffectType.getByName(value);
        if(type != null) return type;
        throw new CommandProcessException("No correct potion effect type found");
    }
}
