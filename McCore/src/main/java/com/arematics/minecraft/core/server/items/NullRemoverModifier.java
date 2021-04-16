package com.arematics.minecraft.core.server.items;

import com.arematics.minecraft.core.items.CoreItem;
import org.springframework.stereotype.Component;

@Component
public class NullRemoverModifier implements CoreItemCreationModifier {

    @Override
    public CoreItem modify(CoreItem start) {
        if(start.getAmount() < 1) return null;
        return start;
    }
}
