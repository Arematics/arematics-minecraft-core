package com.arematics.minecraft.crystals.logic;

import com.arematics.minecraft.core.server.CorePlayer;
import org.springframework.stereotype.Component;

@Component
public class ItemCrystalType extends CrystalType {
    @Override
    public String propertyValue() {
        return "Crystal";
    }

    @Override
    public void execute(CorePlayer player) {

    }
}
